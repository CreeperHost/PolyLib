package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.player.serverdata.PlayerServerDataStore;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsStore;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
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

    /**
     * Load the persisted value for a single {@link PlayerServerDataType} into the store.
     * Called lazily by {@link net.creeperhost.polylib.player.serverdata.PlayerServerDataManager#get}
     * when a type is accessed that was not present in {@code registeredServerTypes} at login time
     * (e.g. because the mod class was not yet initialized when {@code PlayerLoggedInEvent} fired).
     *
     * <p>If no persisted value exists the store is left unmodified; subsequent {@code store.get()}
     * will return the type's default via {@code computeIfAbsent}.
     */
    <T> void loadServerDataForType(UUID playerUUID, ServerPlayer player,
                                    PlayerServerDataStore store, PlayerServerDataType<T> type);

    /**
     * Read a single server-data type from a raw player NBT CompoundTag (for offline player access).
     * The {@code playerNbt} is the top-level tag loaded from {@code <world>/playerdata/<uuid>.dat}.
     * Platform impls read from their own storage key inside that tag
     * (e.g. {@code ForgeData} on NeoForge, {@code fabric:attachments} on Fabric).
     *
     * @return the decoded value, or empty if no value is stored for this type
     */
    <T> Optional<T> readOfflineServerData(CompoundTag playerNbt, PlayerServerDataType<T> type);

    /**
     * Write a single server-data type into a raw player NBT CompoundTag (for offline player access).
     * The {@code playerNbt} is the top-level tag that will be written back to
     * {@code <world>/playerdata/<uuid>.dat}.
     */
    <T> void writeOfflineServerData(CompoundTag playerNbt, PlayerServerDataType<T> type, T value);
}
