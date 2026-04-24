package net.creeperhost.polylib.fabric.registry;

import com.google.common.base.Suppliers;
import net.creeperhost.polylib.registry.PolyRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricPolyRegistry<T> extends PolyRegistry<T> {
    
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final Map<String, Supplier<? extends T>> entries = new LinkedHashMap<>();

    public FabricPolyRegistry(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        super(registryKey, modId);
        this.registryKey = registryKey;
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Supplier<I> supplier) {
        // Memoize so that calling get() on the returned supplier multiple times only initializes the object once
        Supplier<I> memoized = Suppliers.memoize(supplier::get);
        entries.put(name, memoized);
        return memoized;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> Supplier<I> registerWithKey(String name, Function<ResourceKey<T>, ? extends I> factory) {
        ResourceKey<T> key = ResourceKey.create(registryKey, Identifier.fromNamespaceAndPath(modId, name));
        Supplier<I> memoized = Suppliers.memoize(() -> factory.apply(key));
        entries.put(name, (Supplier<? extends T>) memoized);
        return memoized;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
        if (registry == null) {
            throw new IllegalStateException("Failed to find registry for key: " + registryKey.identifier());
        }
        
        entries.forEach((name, supplier) -> {
            Registry.register(registry, Identifier.fromNamespaceAndPath(modId, name), supplier.get());
        });
    }
}
