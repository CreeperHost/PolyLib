package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.player.serverdata.PlayerServerDataStore;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsStore;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Platform service for persisting player data (both client settings and server data).
 * NeoForge impl uses {@code getPersistentData()} (CompoundTag) for both.
 * Fabric impl uses {@code AttachmentRegistry} for both.
 */
public interface IPlayerDataHelper
{
    /** Called when a {@link PlayerClientSettingsType} is registered. Allocates the platform storage slot. */
    void registerType(PlayerClientSettingsType<?> type);

    /**
     * Load all persisted client-settings data for a player into the provided store.
     * Called on PlayerLoggedInEvent / ServerPlayConnectionEvents.JOIN.
     */
    void loadAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store);

    /**
     * Persist all dirty client-settings entries in the store.
     * Must call {@link PlayerClientSettingsStore#clearDirty()} after a successful save.
     */
    void saveAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store);

    /** Called when a {@link PlayerServerDataType} is registered. Allocates the platform storage slot. */
    void registerServerDataType(PlayerServerDataType<?> type);

    /**
     * Load all persisted server-data values for a player into the provided store.
     * Called on PlayerLoggedInEvent / ServerPlayConnectionEvents.JOIN.
     */
    void loadServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store);

    /**
     * Persist all dirty server-data entries in the store.
     * Must call {@link PlayerServerDataStore#clearDirty()} after a successful save.
     */
    void saveServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store);
}
