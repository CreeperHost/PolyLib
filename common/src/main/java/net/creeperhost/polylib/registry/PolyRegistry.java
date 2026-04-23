package net.creeperhost.polylib.registry;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

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
     * Triggers the final registration sequence.
     * On Fabric, this natively registers the queued objects.
     * On NeoForge, this is a no-op as objects are passed directly to the DeferredRegister.
     */
    public abstract void init();
}
