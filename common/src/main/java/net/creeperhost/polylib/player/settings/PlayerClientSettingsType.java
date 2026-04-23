package net.creeperhost.polylib.player.settings;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Typed token representing a registered PlayerClientSetting type.
 * Create via {@link PlayerClientSettingsRegistry#register} and store as a
 * {@code public static final} constant in your mod.
 *
 * @param <T> The value type
 */
public final class PlayerClientSettingsType<T>
{
    private final Identifier id;
    private final StreamCodec<RegistryFriendlyByteBuf, T> codec;
    private final Supplier<T> defaultFactory;
    private final BroadcastScope scope;
    private final boolean copyOnDeath;
    /** Optional translation key for this type's human-readable display name. */
    @Nullable private final String displayNameKey;

    PlayerClientSettingsType(Identifier id,
                             StreamCodec<RegistryFriendlyByteBuf, T> codec,
                             Supplier<T> defaultFactory,
                             BroadcastScope scope,
                             boolean copyOnDeath,
                             @Nullable String displayNameKey)
    {
        this.id = id;
        this.codec = codec;
        this.defaultFactory = defaultFactory;
        this.scope = scope;
        this.copyOnDeath = copyOnDeath;
        this.displayNameKey = displayNameKey;
    }

    public Identifier id()
    {
        return id;
    }

    public StreamCodec<RegistryFriendlyByteBuf, T> codec()
    {
        return codec;
    }

    public Supplier<T> defaultFactory()
    {
        return defaultFactory;
    }

    public BroadcastScope scope()
    {
        return scope;
    }

    public boolean copyOnDeath()
    {
        return copyOnDeath;
    }

    /**
     * Optional translation key for this type's human-readable display name.
     * Null if no display name was registered.
     * Used by UI panels that list available settings types.
     */
    @Nullable
    public String displayNameKey()
    {
        return displayNameKey;
    }

    @Override
    public String toString()
    {
        return "PlayerClientSettingsType[" + id + "]";
    }
}

