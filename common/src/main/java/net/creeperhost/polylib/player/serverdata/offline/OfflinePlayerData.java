package net.creeperhost.polylib.player.serverdata.offline;

import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;

/**
 * Mutable view of a single player's PolyLib server data, valid for either an
 * online or offline player.  Obtained via
 * {@link OfflinePlayerDataAccessor#modify(net.minecraft.server.MinecraftServer, java.util.UUID, java.util.function.Consumer)}.
 *
 * <p>For online players every {@link #get}/{@link #set} call goes directly to
 * {@link net.creeperhost.polylib.player.serverdata.PlayerServerDataManager}.
 * For offline players the view is backed by the loaded {@code ForgeData}
 * {@link net.minecraft.nbt.CompoundTag}; changes are written back to
 * {@code <world>/playerdata/<uuid>.dat} when the {@code modify} call returns.
 */
public interface OfflinePlayerData {

    /**
     * Whether the underlying player is currently online.
     * If {@code true}, changes take effect immediately in the live store.
     */
    boolean isOnline();

    /**
     * Returns the current value for the given type, or the type's default if
     * no value has been saved.
     */
    <T> T get(PlayerServerDataType<T> type);

    /**
     * Writes a new value for the given type.
     * For offline players the change is persisted when the enclosing
     * {@link OfflinePlayerDataAccessor#modify} call returns.
     */
    <T> void set(PlayerServerDataType<T> type, T value);
}
