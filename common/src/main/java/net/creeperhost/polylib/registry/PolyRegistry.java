package net.creeperhost.polylib.registry;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PolyRegistry<T> {

    /** Mod ID passed at creation, used to build MC-style translation keys. */
    protected final String modId;

    /**
     * Registry key path, e.g. {@code "item"}, {@code "block"}, {@code "entity_type"}.
     * Combined with modId and entry name to form the standard MC translation key
     * ({@code "<path>.<modId>.<name>"}), matching Minecraft's default
     * {@code Block.getDescriptionId()} / {@code Item.getDescriptionId()} patterns.
     */
    protected final String langKeyPrefix;

    protected PolyRegistry(ResourceKey<? extends Registry<T>> registryKey, String modId)
    {
        this.modId = modId;
        this.langKeyPrefix = registryKey.identifier().getPath();
    }

    /**
     * Creates a new PolyRegistry for the given registry key and mod ID.
     */
    public static <T> PolyRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return Services.REGISTRY.create(registryKey, modId);
    }

    /**
     * Registers a new object to this registry queue.
     *
     * @param name     The registry name (path).
     * @param supplier A supplier providing the object to register.
     * @return A supplier that will return the registered object once initialized.
     */
    public abstract <I extends T> Supplier<I> register(String name, Supplier<I> supplier);

    /**
     * Registers a new object and contributes its default English translation to PolyLib's
     * lang datagen system.
     *
     * <p>The translation key is derived automatically using Minecraft's standard pattern:
     * {@code "<registryPath>.<modId>.<name>"}, e.g. {@code "item.mymod.void_blade"}.
     * This matches the default key returned by {@code Item.getDescriptionId()} and
     * {@code Block.getDescriptionId()}.
     *
     * <p>Mod lang providers extending {@code PolyLibLangProvider} receive this entry
     * automatically — no manual {@code add()} call needed.
     *
     * @param name           Registry name (path), e.g. {@code "void_blade"}
     * @param defaultEnglish Default English display name, e.g. {@code "Void Blade"}
     * @param supplier       Object supplier
     * @return A supplier returning the registered object
     */
    public final <I extends T> Supplier<I> register(String name, String defaultEnglish, Supplier<I> supplier)
    {
        PolyLangContributions.contribute(langKeyPrefix + "." + modId + "." + name, defaultEnglish);
        return register(name, supplier);
    }

    /**
     * Registers a new object, passing its {@link ResourceKey} to the factory before construction.
     *
     * <p>Use this (or the {@link #registerItem}/{@link #registerBlock} helpers) whenever the
     * object needs its registry key at construction time. On NeoForge this is required for
     * {@link Item} and {@link Block} so that {@code Item.Properties#setId} /
     * {@code BlockBehaviour.Properties#setId} can be called before the constructor runs.
     *
     * @param name    Registry name (path)
     * @param factory Factory that receives the entry's {@code ResourceKey} and returns the object
     * @return A supplier returning the registered object
     */
    public abstract <I extends T> Supplier<I> registerWithKey(String name, Function<ResourceKey<T>, ? extends I> factory);

    /**
     * Registers an {@link Item} subclass, automatically injecting the registry key into
     * {@link Item.Properties} via {@code setId()} before the constructor runs.
     *
     * <p>Use this instead of {@link #register(String, Supplier)} for all item registrations so
     * that NeoForge's item-id requirement is satisfied.
     *
     * @param name    Registry name (path), e.g. {@code "void_blade"}
     * @param factory Constructor reference or lambda accepting pre-keyed {@link Item.Properties}
     * @return A supplier returning the registered item
     */
    @SuppressWarnings("unchecked")
    public final <I extends T> Supplier<I> registerItem(String name, Function<Item.Properties, ? extends I> factory)
    {
        return registerWithKey(name, key ->
                factory.apply(new Item.Properties().setId((ResourceKey<Item>) (ResourceKey<?>) key)));
    }

    /**
     * Registers an {@link Item} subclass with a default English translation, automatically
     * injecting the registry key into {@link Item.Properties} via {@code setId()}.
     *
     * @param name           Registry name (path), e.g. {@code "void_blade"}
     * @param defaultEnglish Default English display name, e.g. {@code "Void Blade"}
     * @param factory        Constructor reference or lambda accepting pre-keyed {@link Item.Properties}
     * @return A supplier returning the registered item
     */
    @SuppressWarnings("unchecked")
    public final <I extends T> Supplier<I> registerItem(String name, String defaultEnglish, Function<Item.Properties, ? extends I> factory)
    {
        PolyLangContributions.contribute(langKeyPrefix + "." + modId + "." + name, defaultEnglish);
        return registerWithKey(name, key ->
                factory.apply(new Item.Properties().setId((ResourceKey<Item>) (ResourceKey<?>) key)));
    }

    /**
     * Registers a {@link Block} subclass, automatically injecting the registry key into
     * {@link BlockBehaviour.Properties} via {@code setId()} before the constructor runs.
     *
     * @param name    Registry name (path), e.g. {@code "copper_ore"}
     * @param factory Constructor reference or lambda accepting pre-keyed {@link BlockBehaviour.Properties}
     * @return A supplier returning the registered block
     */
    @SuppressWarnings("unchecked")
    public final <I extends T> Supplier<I> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends I> factory)
    {
        return registerWithKey(name, key ->
                factory.apply(BlockBehaviour.Properties.of().setId((ResourceKey<Block>) (ResourceKey<?>) key)));
    }

    /**
     * Registers a {@link Block} subclass with a default English translation, automatically
     * injecting the registry key into {@link BlockBehaviour.Properties} via {@code setId()}.
     *
     * @param name           Registry name (path), e.g. {@code "copper_ore"}
     * @param defaultEnglish Default English display name, e.g. {@code "Copper Ore"}
     * @param factory        Constructor reference or lambda accepting pre-keyed {@link BlockBehaviour.Properties}
     * @return A supplier returning the registered block
     */
    @SuppressWarnings("unchecked")
    public final <I extends T> Supplier<I> registerBlock(String name, String defaultEnglish, Function<BlockBehaviour.Properties, ? extends I> factory)
    {
        PolyLangContributions.contribute(langKeyPrefix + "." + modId + "." + name, defaultEnglish);
        return registerWithKey(name, key ->
                factory.apply(BlockBehaviour.Properties.of().setId((ResourceKey<Block>) (ResourceKey<?>) key)));
    }

    /**
     * Triggers the final registration sequence.
     * On Fabric, this natively registers the queued objects.
     * On NeoForge, this is a no-op as objects are passed directly to the DeferredRegister.
     */
    public abstract void init();
}
