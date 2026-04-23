package net.creeperhost.polylib.player.serverdata;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

/**
 * Typed token representing a registered server-authoritative player data type.
 *
 * <p>Create via {@link PlayerServerDataRegistry#register} and store as a
 * {@code public static final} constant in your mod.
 *
 * <p>This is the server-authority counterpart to {@code PlayerClientSettingsType}.
 * The server owns the value; the client receives read-only S2C syncs for HUD use.
 *
 * @param <T> The value type
 */
public final class PlayerServerDataType<T>
{
    private final String id;
    /** DFU Codec used for NBT persistence. */
    private final Codec<T> nbtCodec;
    /**
     * StreamCodec used to serialize the value for S2C sync to the owning player.
     * {@code null} means no client sync — value is server-only.
     */
    @Nullable
    private final StreamCodec<RegistryFriendlyByteBuf, T> syncCodec;
    private final Supplier<T> defaultFactory;
    private final boolean copyOnDeath;

    PlayerServerDataType(String id,
                         Codec<T> nbtCodec,
                         @Nullable StreamCodec<RegistryFriendlyByteBuf, T> syncCodec,
                         Supplier<T> defaultFactory,
                         boolean copyOnDeath)
    {
        this.id = id;
        this.nbtCodec = nbtCodec;
        this.syncCodec = syncCodec;
        this.defaultFactory = defaultFactory;
        this.copyOnDeath = copyOnDeath;
    }

    public String id() { return id; }
    public Codec<T> nbtCodec() { return nbtCodec; }
    @Nullable public StreamCodec<RegistryFriendlyByteBuf, T> syncCodec() { return syncCodec; }
    public Supplier<T> defaultFactory() { return defaultFactory; }
    public boolean copyOnDeath() { return copyOnDeath; }
    /** Whether this type sends S2C sync packets to the owning player. */
    public boolean syncsToClient() { return syncCodec != null; }

    @Override
    public String toString() { return "PlayerServerDataType[" + id + "]"; }
}
