package net.creeperhost.polylib.neoforge.registry;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.creeperhost.polylib.registry.PolyRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoPolyRegistry<T> extends PolyRegistry<T> {
    
    // Keeps track of all created registries so they can be bulk-attached to the ModEventBus
    private static final List<NeoPolyRegistry<?>> ALL_REGISTRIES = new ArrayList<>();
    
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final DeferredRegister<T> deferredRegister;

    public NeoPolyRegistry(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        super(registryKey, modId);
        this.registryKey = registryKey;
        this.deferredRegister = DeferredRegister.create(registryKey, modId);
        ALL_REGISTRIES.add(this);
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Supplier<I> supplier) {
        // Delegate directly to NeoForge's DeferredRegister
        return deferredRegister.register(name, supplier);
    }

    @Override
    public <I extends T> Supplier<I> registerWithKey(String name, Function<ResourceKey<T>, ? extends I> factory) {
        return deferredRegister.register(name, key -> factory.apply(ResourceKey.create(registryKey, key)));
    }

    @Override
    public void init() {
        // No-op on NeoForge. The items are already in the DeferredRegister queue.
        // We attach them to the bus via registerToBus() below instead.
    }

    @Override
    @SuppressWarnings("unchecked")
    public Supplier<CreativeModeTab> registerCreativeTab(String name, String defaultEnglish,
            Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems) {
        String key = "itemGroup." + modId + "." + name;
        PolyLangContributions.contribute(key, defaultEnglish);
        Supplier<T> typed = () -> (T) CreativeModeTab.builder()
                .title(Component.translatable(key))
                .icon(icon)
                .displayItems(displayItems)
                .build();
        return (Supplier<CreativeModeTab>) register(name, typed);
    }

    /**
     * Explicitly called from the Mod constructor: NeoPolyRegistry.registerToBus(eventBus);
     */
    public static void registerToBus(IEventBus modEventBus) {
        for (NeoPolyRegistry<?> registry : ALL_REGISTRIES) {
            registry.deferredRegister.register(modEventBus);
        }
    }
}
