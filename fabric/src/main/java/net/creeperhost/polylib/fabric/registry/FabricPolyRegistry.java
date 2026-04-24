package net.creeperhost.polylib.fabric.registry;

import com.google.common.base.Suppliers;
import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.creeperhost.polylib.registry.PolyRegistry;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

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

    /**
     * Overrides the base implementation to use {@link FabricCreativeModeTab#builder()} instead of
     * {@link net.minecraft.world.item.CreativeModeTab#builder()}. Fabric requires its own builder
     * so that the tab participates in Fabric API's display-item events.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Supplier<CreativeModeTab> registerCreativeTab(String name, String defaultEnglish,
            Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems) {
        String key = "itemGroup." + modId + "." + name;
        PolyLangContributions.contribute(key, defaultEnglish);
        Supplier<T> typed = () -> (T) FabricCreativeModeTab.builder()
                .title(Component.translatable(key))
                .icon(icon)
                .displayItems(displayItems)
                .build();
        return (Supplier<CreativeModeTab>) register(name, typed);
    }
}
