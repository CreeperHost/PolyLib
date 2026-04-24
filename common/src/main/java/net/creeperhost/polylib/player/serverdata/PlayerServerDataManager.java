package net.creeperhost.polylib.player.serverdata;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central server-side manager for {@link PlayerServerDataType} values.
 *
 * <p>Handles NBT persistence (via {@code IPlayerDataHelper}), optional S2C sync
 * to the owning player, and player lifecycle (login / logout / respawn).
 *
 * <p>Server is always the authoritative source. Use {@link #get} / {@link #set}
 * from server-side code only. Clients receive a read-only copy via
 * {@link PlayerServerDataClientCache} when the type's {@code syncCodec} is non-null.
 */
public final class PlayerServerDataManager
{
    private static final Map<UUID, PlayerServerDataStore> STORES = new ConcurrentHashMap<>();

    private PlayerServerDataManager() {}

    /** Called when a player joins. Loads persisted data and syncs syncable types to the owner. */
    public static void onPlayerLogin(ServerPlayer player)
    {
        UUID uuid = player.getUUID();
        PlayerServerDataStore store = new PlayerServerDataStore();
        STORES.put(uuid, store);
        Services.PLAYER_DATA.loadServerData(uuid, player, store);

        for (PlayerServerDataType<?> type : PlayerServerDataRegistry.getAll())
        {
            if (type.syncsToClient())
                syncToOwner(player, type, store);
        }
    }

    /**
     * Called when a player leaves. Removes the in-memory store.
     * Data is already persisted on every {@link #set} call.
     */
    public static void onPlayerLogout(UUID playerUUID)
    {
        STORES.remove(playerUUID);
    }

    /**
     * Called on respawn. Copies {@code copyOnDeath} values from old store to new player.
     * On NeoForge the UUID is stable so old/new UUID are the same; pass it for both.
     * On Fabric, platform-level {@code copyOnDeath()} attachment handles storage automatically;
     * this call re-syncs to the client after respawn.
     */
    public static void onPlayerRespawn(UUID oldUUID, ServerPlayer newPlayer)
    {
        PlayerServerDataStore oldStore = STORES.get(oldUUID);
        if (oldStore == null) return;

        PlayerServerDataStore newStore = STORES.computeIfAbsent(newPlayer.getUUID(),
                k -> new PlayerServerDataStore());

        for (PlayerServerDataType<?> type : PlayerServerDataRegistry.getAll())
        {
            if (type.copyOnDeath() && oldStore.has(type))
                copyTyped(type, oldStore, newStore);
        }

        // Re-sync all syncable types to the client after respawn
        for (PlayerServerDataType<?> type : PlayerServerDataRegistry.getAll())
        {
            if (type.syncsToClient())
                syncToOwner(newPlayer, type, newStore);
        }
    }

    /**
     * Returns the current value for a player. Server-side only.
     * Returns the type's default if the player has no active store.
     *
     * <p><b>Lazy-load:</b> if the store exists but has never seen this type (i.e. the type was
     * registered after {@code PlayerLoggedInEvent} fired due to Java's lazy class initialisation),
     * we attempt a single NBT read from the player's persistent data before returning the default.
     * This makes registration order irrelevant in production and removes the need for mods to call
     * a no-op {@code init()} method to force-load their registry class.
     */
    public static <T> T get(ServerPlayer player, PlayerServerDataType<T> type)
    {
        PlayerServerDataStore store = STORES.get(player.getUUID());
        if (store == null) return type.defaultFactory().get();
        // Lazy load: type registered after login (late class init) — pull from NBT now.
        if (!store.has(type))
            Services.PLAYER_DATA.loadServerDataForType(player.getUUID(), player, store, type);
        return store.get(type);
    }

    /**
     * Set a value server-side. Persists immediately and syncs to owner if the type
     * has a {@code syncCodec}. <b>Server-side only.</b>
     */
    public static <T> void set(ServerPlayer player, PlayerServerDataType<T> type, T value)
    {
        UUID uuid = player.getUUID();
        PlayerServerDataStore store = STORES.computeIfAbsent(uuid, k -> new PlayerServerDataStore());
        store.set(type, value);
        Services.PLAYER_DATA.saveServerData(uuid, player, store);
        if (type.syncsToClient())
            syncToOwner(player, type, store);
    }

    private static <T> void syncToOwner(ServerPlayer owner,
                                         PlayerServerDataType<T> type,
                                         PlayerServerDataStore store)
    {
        StreamCodecHolder<T> holder = new StreamCodecHolder<>(type);
        if (!holder.hasCodec()) return;

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), owner.level().registryAccess());
        holder.encode(buf, store.get(type));
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        Services.NETWORK.sendToPlayer(owner, new SyncPlayerServerDataS2CPayload(type.id(), data));
    }

    @SuppressWarnings("unchecked")
    private static <T> void copyTyped(PlayerServerDataType<T> type,
                                       PlayerServerDataStore src,
                                       PlayerServerDataStore dst)
    {
        dst.load(type, src.get(type));
    }

    /**
     * Helper to avoid raw-type warnings when calling syncCodec encode/decode.
     * The type erasure is safe because we only ever create this from a typed
     * {@link PlayerServerDataType}{@code <T>}.
     */
    @SuppressWarnings("unchecked")
    private static final class StreamCodecHolder<T>
    {
        private final net.minecraft.network.codec.StreamCodec<RegistryFriendlyByteBuf, T> codec;

        StreamCodecHolder(PlayerServerDataType<T> type) { this.codec = type.syncCodec(); }
        boolean hasCodec() { return codec != null; }
        void encode(RegistryFriendlyByteBuf buf, T value) { codec.encode(buf, value); }
    }
}
