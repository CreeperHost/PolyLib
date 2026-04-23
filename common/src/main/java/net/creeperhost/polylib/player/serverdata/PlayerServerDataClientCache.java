package net.creeperhost.polylib.player.serverdata;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side read-only cache of server-authoritative player data values.
 *
 * <p>Values are received via {@link SyncPlayerServerDataS2CPayload} and decoded on demand
 * using the type's {@code syncCodec}. This cache is keyed by type ID only (not UUID)
 * because these values are always for the local/owning player.
 *
 * <p>Typical use: HUD rendering, client-side power availability checks.
 * Never write to this; mutations go through {@link PlayerServerDataManager#set} on the server.
 */
public final class PlayerServerDataClientCache
{
    // typeId → raw bytes as received from S2C payload (encoded via syncCodec)
    private static final Map<Identifier, byte[]> RAW = new ConcurrentHashMap<>();

    private PlayerServerDataClientCache() {}

    /**
     * Store raw bytes received via {@link SyncPlayerServerDataS2CPayload}.
     * Called from the S2C packet handler on the client.
     */
    public static void receive(Identifier typeId, byte[] data)
    {
        RAW.put(typeId, data);
    }

    /**
     * Get the cached value for a type, decoded via the type's {@code syncCodec}.
     * Returns the type's default if no data has been received yet.
     *
     * <p>This method is inexpensive but not free — it deserializes on every call.
     * Cache the result in your renderer if called per-frame.
     */
    public static <T> T get(PlayerServerDataType<T> type)
    {
        if (type.syncCodec() == null) return type.defaultFactory().get();
        byte[] data = RAW.get(type.id());
        if (data == null) return type.defaultFactory().get();

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return type.defaultFactory().get();

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(data),
                mc.getConnection().registryAccess());
        return type.syncCodec().decode(buf);
    }

    /**
     * Clear all cached data. Call on client disconnect so stale values don't persist.
     */
    public static void clear()
    {
        RAW.clear();
    }
}
