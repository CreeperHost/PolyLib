package net.creeperhost.polylib.player.settings;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache of PlayerClientSettings values received via S2C sync packets.
 * Replaces per-mod caches like {@code PlayerAppearanceCache}.
 */
public final class PlayerClientSettingsClientCache
{
    // Outer key: player UUID. Inner key: type ID. Value: raw serialized bytes.
    private static final Map<UUID, Map<String, byte[]>> RAW = new ConcurrentHashMap<>();

    private PlayerClientSettingsClientCache() {}

    /**
     * Store raw bytes for a player+type. Called from S2C packet handler.
     */
    public static void receive(UUID playerUUID, String typeId, byte[] data)
    {
        RAW.computeIfAbsent(playerUUID, k -> new ConcurrentHashMap<>()).put(typeId, data);
    }

    /**
     * Get the cached value for a player and type, or the default if not cached.
     */
    public static <T> T getFor(UUID playerUUID, PlayerClientSettingsType<T> type)
    {
        Map<String, byte[]> playerMap = RAW.get(playerUUID);
        if (playerMap == null) return type.defaultFactory().get();
        byte[] data = playerMap.get(type.id());
        if (data == null) return type.defaultFactory().get();

        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null) return type.defaultFactory().get();

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(data),
                mc.getConnection().registryAccess());
        return type.codec().decode(buf);
    }

    /**
     * Remove cached data for one player (e.g. on disconnect/leave).
     */
    public static void evict(UUID playerUUID)
    {
        RAW.remove(playerUUID);
    }

    /**
     * Clear all cached data. Called on client disconnect.
     */
    public static void clear()
    {
        RAW.clear();
    }
}
