package net.creeperhost.polylib.player.serverdata;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Registry for {@link PlayerServerDataType} tokens.
 * All registration must happen before the server starts (mod constructor / init).
 */
public final class PlayerServerDataRegistry
{
    private static final Map<Identifier, PlayerServerDataType<?>> BY_ID = new LinkedHashMap<>();

    private PlayerServerDataRegistry() {}

    /**
     * Register a server-authoritative player data type with optional S2C sync to owner.
     *
     * @param id             Namespaced ID, e.g. {@code "discrafthonored:abilities"}
     * @param nbtCodec       DFU {@link Codec} used for NBT persistence
     * @param syncCodec      {@link StreamCodec} for S2C sync to the owning player.
     *                       Pass {@code null} for server-only data (no client HUD sync).
     * @param defaultFactory Supplier of a fresh default value when no data exists
     * @param copyOnDeath    Whether the value survives player death/respawn
     * @return The typed token — store as a {@code public static final} constant
     */
    public static <T> PlayerServerDataType<T> register(Identifier id,
                                                        Codec<T> nbtCodec,
                                                        @Nullable StreamCodec<RegistryFriendlyByteBuf, T> syncCodec,
                                                        Supplier<T> defaultFactory,
                                                        boolean copyOnDeath)
    {
        if (BY_ID.containsKey(id))
            throw new IllegalStateException("PlayerServerDataType already registered: " + id);

        PlayerServerDataType<T> type = new PlayerServerDataType<>(id, nbtCodec, syncCodec, defaultFactory, copyOnDeath);
        BY_ID.put(id, type);
        Services.PLAYER_DATA.registerServerDataType(type);
        return type;
    }

    /**
     * Convenience overload — no S2C sync (server-only data).
     */
    public static <T> PlayerServerDataType<T> register(Identifier id,
                                                        Codec<T> nbtCodec,
                                                        Supplier<T> defaultFactory,
                                                        boolean copyOnDeath)
    {
        return register(id, nbtCodec, null, defaultFactory, copyOnDeath);
    }

    /** Returns all registered types in registration order. */
    public static Collection<PlayerServerDataType<?>> getAll()
    {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    /** Lookup by namespaced ID — used by packet handlers. */
    public static Optional<PlayerServerDataType<?>> byId(Identifier id)
    {
        return Optional.ofNullable(BY_ID.get(id));
    }
}
