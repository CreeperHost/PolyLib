package net.creeperhost.polylib.player.settings;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central server-side manager for PlayerClientSettings.
 * Handles persistence, sync, and incoming C2S updates.
 */
public final class PlayerClientSettingsManager
{
    private static final Map<UUID, PlayerClientSettingsStore> STORES = new ConcurrentHashMap<>();

    private PlayerClientSettingsManager() {}

    /** Called when a player joins. Loads persisted data and syncs to appropriate clients. */
    public static void onPlayerLogin(ServerPlayer player)
    {
        UUID uuid = player.getUUID();
        PlayerClientSettingsStore store = new PlayerClientSettingsStore();
        STORES.put(uuid, store);
        Services.PLAYER_DATA.loadAll(uuid, player, store);

        // Sync to the joining player (their own settings back to themselves)
        // and broadcast to others per scope
        for (PlayerClientSettingsType<?> type : PlayerClientSettingsRegistry.getAll())
        {
            syncToRelevant(player, type, store, true);
        }
    }

    /** Called when a player leaves. Store is discarded — persistence happens on each {@link #set} call. */
    public static void onPlayerLogout(UUID playerUUID)
    {
        STORES.remove(playerUUID);
    }

    /** Called on respawn. Copies settings from old UUID to new player if copyOnDeath is true. */
    public static void onPlayerRespawn(UUID oldUUID, ServerPlayer newPlayer)
    {
        PlayerClientSettingsStore oldStore = STORES.get(oldUUID);
        if (oldStore == null) return;

        PlayerClientSettingsStore newStore = STORES.computeIfAbsent(newPlayer.getUUID(),
                k -> new PlayerClientSettingsStore());

        for (PlayerClientSettingsType<?> type : PlayerClientSettingsRegistry.getAll())
        {
            if (type.copyOnDeath() && oldStore.has(type))
            {
                copyTyped(type, oldStore, newStore);
            }
        }

        // Re-sync after respawn
        for (PlayerClientSettingsType<?> type : PlayerClientSettingsRegistry.getAll())
        {
            syncToRelevant(newPlayer, type, newStore, false);
        }
    }

    /** Called when a player starts tracking another. Backfills TRACKING_RANGE data. */
    public static void syncTrackingRange(ServerPlayer tracked, ServerPlayer tracker)
    {
        UUID trackedUUID = tracked.getUUID();
        PlayerClientSettingsStore store = STORES.get(trackedUUID);
        if (store == null) return;

        for (PlayerClientSettingsType<?> type : PlayerClientSettingsRegistry.getAll())
        {
            if (type.scope() == BroadcastScope.TRACKING_RANGE)
            {
                sendToPlayer(tracker, trackedUUID, type, store);
            }
        }
    }

    /** Called when server receives {@link UpdatePlayerClientSettingC2SPayload}. */
    public static void applyFromClient(ServerPlayer player, Identifier typeId, byte[] data)
    {
        PlayerClientSettingsRegistry.byId(typeId).ifPresent(type -> {
            UUID uuid = player.getUUID();
            PlayerClientSettingsStore store = STORES.computeIfAbsent(uuid,
                    k -> new PlayerClientSettingsStore());
            applyTyped(type, player, store, data);
        });
    }

    /** Returns the current value for a player. Server-side only. */
    public static <T> T get(UUID playerUUID, PlayerClientSettingsType<T> type)
    {
        PlayerClientSettingsStore store = STORES.get(playerUUID);
        if (store == null) return type.defaultFactory().get();
        return store.get(type);
    }

    /** Sets a value server-side. Persists and broadcasts per {@link BroadcastScope}. */
    public static <T> void set(ServerPlayer player, PlayerClientSettingsType<T> type, T value)
    {
        UUID uuid = player.getUUID();
        PlayerClientSettingsStore store = STORES.computeIfAbsent(uuid,
                k -> new PlayerClientSettingsStore());
        store.set(type, value);
        Services.PLAYER_DATA.saveAll(uuid, player, store);
        syncToRelevant(player, type, store, false);
    }

    /**
     * Sends the current value to the server. Client-side.
     * Pass {@code Minecraft.getInstance().getConnection().registryAccess()} as the registry access.
     */
    public static <T> void sendToServer(PlayerClientSettingsType<T> type, T value, RegistryAccess registryAccess)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                registryAccess);
        type.codec().encode(buf, value);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        Services.NETWORK.sendToServer(new UpdatePlayerClientSettingC2SPayload(type.id(), data));
    }

    @SuppressWarnings("unchecked")
    private static <T> void applyTyped(PlayerClientSettingsType<T> type,
                                       ServerPlayer player,
                                       PlayerClientSettingsStore store,
                                       byte[] data)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(data),
                player.level().registryAccess());
        T value = type.codec().decode(buf);
        store.set(type, value);
        Services.PLAYER_DATA.saveAll(player.getUUID(), player, store);
        syncToRelevant(player, type, store, false);
    }

    @SuppressWarnings("unchecked")
    private static <T> void copyTyped(PlayerClientSettingsType<T> type,
                                      PlayerClientSettingsStore src,
                                      PlayerClientSettingsStore dst)
    {
        dst.load(type, src.get(type));
    }

    private static <T> void syncToRelevant(ServerPlayer owner,
                                           PlayerClientSettingsType<T> type,
                                           PlayerClientSettingsStore store,
                                           boolean isLogin)
    {
        BroadcastScope scope = type.scope();
        if (scope == BroadcastScope.SERVER_ONLY) return;

        UUID uuid = owner.getUUID();

        switch (scope)
        {
            case SELF_ONLY:
                sendToPlayer(owner, uuid, type, store);
                break;
            case ALL_ONLINE:
                ((net.minecraft.server.level.ServerLevel) owner.level()).getServer().getPlayerList().getPlayers().forEach(p ->
                        sendToPlayer(p, uuid, type, store));
                break;
            case TRACKING_RANGE:
                if (isLogin) sendToPlayer(owner, uuid, type, store);
                if (!isLogin)
                {
                    ((net.minecraft.server.level.ServerLevel) owner.level()).getServer().getPlayerList().getPlayers().forEach(p -> {
                        if (p.getUUID().equals(uuid)) return;
                        if (p.level() == owner.level()
                                && p.distanceToSqr(owner) <= 128 * 128)
                        {
                            sendToPlayer(p, uuid, type, store);
                        }
                    });
                    sendToPlayer(owner, uuid, type, store);
                }
                break;
            default:
                break;
        }
    }

    private static <T> void sendToPlayer(ServerPlayer target,
                                         UUID ownerUUID,
                                         PlayerClientSettingsType<T> type,
                                         PlayerClientSettingsStore store)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                ((net.minecraft.server.level.ServerLevel) target.level()).getServer().registryAccess());
        type.codec().encode(buf, store.get(type));
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        Services.NETWORK.sendToPlayer(target,
                new PlayerClientSettingSyncS2CPayload(ownerUUID, type.id(), data));
    }
}
