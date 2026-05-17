package net.creeperhost.polylib.player.serverdata.offline;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Read/write PolyLib {@link PlayerServerDataType} values for both online and
 * offline players.
 *
 * <h2>Online players</h2>
 * All operations delegate to the live
 * {@link PlayerServerDataManager} so the in-memory store is never bypassed.
 *
 * <h2>Offline players</h2>
 * Data is read from / written to the player's {@code .dat} file at
 * {@code <world>/playerdata/<uuid>.dat}.  The exact NBT structure used is
 * platform-specific: NeoForge stores data inside the {@code ForgeData} compound;
 * Fabric uses the {@code fabric:attachments} compound.
 *
 * <h2>Thread safety</h2>
 * Must be called from the server tick thread.  No locking is performed because
 * Minecraft's server is single-threaded; concurrent calls from other threads
 * (e.g. the network thread) are unsafe regardless.
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * // One-shot read (returns default if no data)
 * PlayerAbilities ab = OfflinePlayerDataAccessor.get(server, uuid, ModPlayerData.PLAYER_ABILITIES);
 *
 * // One-shot write
 * OfflinePlayerDataAccessor.set(server, uuid, ModPlayerData.PLAYER_ABILITIES, newAbilities);
 *
 * // Atomic read-modify-write (single file I/O pass for offline players)
 * OfflinePlayerDataAccessor.modify(server, uuid, data -> {
 *     PlayerAbilities ab = data.get(ModPlayerData.PLAYER_ABILITIES);
 *     ab.addPower(SomePower.ID);
 *     data.set(ModPlayerData.PLAYER_ABILITIES, ab);
 * });
 * }</pre>
 */
public final class OfflinePlayerDataAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflinePlayerDataAccessor.class);

    private OfflinePlayerDataAccessor() {}

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Read one data type for a player (online or offline).
     * Returns the type's default if the player has no saved value.
     *
     * @param server the running server instance
     * @param uuid   target player UUID
     * @param type   the data type to read
     */
    public static <T> T get(MinecraftServer server, UUID uuid, PlayerServerDataType<T> type) {
        ServerPlayer online = server.getPlayerList().getPlayer(uuid);
        if (online != null) return PlayerServerDataManager.get(online, type);

        return readPlayerNbt(server, uuid)
                .map(nbt -> Services.PLAYER_DATA.readOfflineServerData(nbt, type).orElse(type.defaultFactory().get()))
                .orElse(type.defaultFactory().get());
    }

    /**
     * Write one data type for a player (online or offline).
     * For online players, also syncs if the type has a sync codec.
     *
     * @return {@code true} if the write succeeded; {@code false} if the player
     *         file was not found or an I/O error occurred.
     */
    public static <T> boolean set(MinecraftServer server, UUID uuid, PlayerServerDataType<T> type, T value) {
        ServerPlayer online = server.getPlayerList().getPlayer(uuid);
        if (online != null) {
            PlayerServerDataManager.set(online, type, value);
            return true;
        }
        return modifyFile(server, uuid, nbt -> Services.PLAYER_DATA.writeOfflineServerData(nbt, type, value));
    }

    /**
     * Atomic read-modify-write in a single file I/O pass (offline) or direct
     * store access (online).
     *
     * <p>The supplied {@code consumer} receives an {@link OfflinePlayerData} view.
     * Call {@link OfflinePlayerData#get} and {@link OfflinePlayerData#set} freely
     * inside it; for offline players the file is written once when the consumer
     * returns.
     *
     * @return {@code true} if the operation succeeded.
     */
    public static boolean modify(MinecraftServer server, UUID uuid, Consumer<OfflinePlayerData> consumer) {
        ServerPlayer online = server.getPlayerList().getPlayer(uuid);
        if (online != null) {
            consumer.accept(new OnlineView(online));
            return true;
        }
        return modifyFile(server, uuid, nbt -> consumer.accept(new FileView(nbt)));
    }

    // ── File I/O ──────────────────────────────────────────────────────────────

    /**
     * Load the raw player NBT CompoundTag from disk without writing back.
     * Returns empty if the file does not exist or cannot be read.
     */
    private static Optional<CompoundTag> readPlayerNbt(MinecraftServer server, UUID uuid) {
        Path file = resolvePlayerFile(server, uuid);
        if (!Files.exists(file)) return Optional.empty();
        try (var in = Files.newInputStream(file)) {
            return Optional.of(NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap()));
        } catch (IOException e) {
            LOGGER.warn("[OfflinePlayerDataAccessor] Failed to read {}: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Load → mutate → write the player NBT for an offline player.
     * If the file does not exist or a read/write error occurs, logs a warning
     * and returns {@code false}.
     */
    private static boolean modifyFile(MinecraftServer server, UUID uuid, Consumer<CompoundTag> mutation) {
        Path file = resolvePlayerFile(server, uuid);
        if (!Files.exists(file)) {
            LOGGER.warn("[OfflinePlayerDataAccessor] Player file not found for {}", uuid);
            return false;
        }
        try (var in = Files.newInputStream(file)) {
            CompoundTag playerNbt = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
            mutation.accept(playerNbt);
            NbtIo.writeCompressed(playerNbt, file);
            return true;
        } catch (IOException e) {
            LOGGER.warn("[OfflinePlayerDataAccessor] Failed to modify {}: {}", file, e.getMessage());
            return false;
        }
    }

    /**
     * Resolves the path to a player's .dat file at
     * {@code <level_root>/playerdata/<uuid>.dat}.
     */
    private static Path resolvePlayerFile(MinecraftServer server, UUID uuid) {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("playerdata")
                .resolve(uuid.toString() + ".dat");
    }

    // ── OfflinePlayerData view implementations ────────────────────────────────

    /** Online view: delegates directly to the live PlayerServerDataManager. */
    private static final class OnlineView implements OfflinePlayerData {
        private final ServerPlayer player;

        OnlineView(ServerPlayer player) { this.player = player; }

        @Override public boolean isOnline() { return true; }

        @Override
        public <T> T get(PlayerServerDataType<T> type) {
            return PlayerServerDataManager.get(player, type);
        }

        @Override
        public <T> void set(PlayerServerDataType<T> type, T value) {
            PlayerServerDataManager.set(player, type, value);
        }
    }

    /**
     * Offline view: reads from / writes to an in-memory player NBT
     * {@link CompoundTag}.  The {@link #modifyFile} caller writes it back to
     * disk after the consumer returns.
     */
    private static final class FileView implements OfflinePlayerData {
        private final CompoundTag playerNbt;

        FileView(CompoundTag playerNbt) { this.playerNbt = playerNbt; }

        @Override public boolean isOnline() { return false; }

        @Override
        public <T> T get(PlayerServerDataType<T> type) {
            return Services.PLAYER_DATA.readOfflineServerData(playerNbt, type).orElse(type.defaultFactory().get());
        }

        @Override
        public <T> void set(PlayerServerDataType<T> type, T value) {
            Services.PLAYER_DATA.writeOfflineServerData(playerNbt, type, value);
        }
    }
}

