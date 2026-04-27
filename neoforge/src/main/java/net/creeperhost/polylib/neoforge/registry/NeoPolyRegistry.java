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
     * Call from your mod constructor, passing both the event bus and your mod ID so that only
     * registries belonging to your mod are attached.
     *
     * <pre>{@code NeoPolyRegistry.registerToBus(bus, MyMod.MOD_ID);}</pre>
     */
    public static void registerToBus(IEventBus modEventBus, String modId) {
        List<NeoPolyRegistry<?>> copy = new ArrayList<>(ALL_REGISTRIES);
        for (NeoPolyRegistry<?> registry : copy) {
            if (registry.modId.equals(modId)) {
                registry.deferredRegister.register(modEventBus);
            }
        }
    }

    /**
     * @deprecated Use {@link #registerToBus(IEventBus, String)} to avoid cross-mod registration conflicts.
     */
    @Deprecated
    public static void registerToBus(IEventBus modEventBus) {
        List<NeoPolyRegistry<?>> copy = new ArrayList<>(ALL_REGISTRIES);
        for (NeoPolyRegistry<?> registry : copy) {
            registry.deferredRegister.register(modEventBus);
        }
    }
}
