package net.creeperhost.polylib.chunkmap.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.chunkmap.common.network.*;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Server-side coordinator for the PolyLib chunk-map feature.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Maintains a per-dimension set of watching player UUIDs.</li>
 *   <li>Sends {@link PolyChunkMapDataPayload} diffs each tick.</li>
 *   <li>Handles {@link PolyChunkMapStartPayload}, {@link PolyChunkMapStopPayload},
 *       and {@link PolyChunkMapRefreshPayload} from clients.</li>
 *   <li>Sends {@link PolyChunkMapHelloPayload}/{@link PolyChunkMapByePayload}
 *       on join/op-change based on permission checks.</li>
 * </ul>
 *
 * <h3>Permission model</h3>
 * On dedicated servers, access is gated by OP level 3+ <em>unless</em> the
 * {@link PolyChunkMapGamerule#OPEN_TO_ALL} gamerule is set to {@code true}.
 * In single-player/LAN everyone is permitted.
 */
public final class PolyChunkMapServer
{
    private static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME + "/ChunkMap");

    private static @Nullable PolyChunkMapServer INSTANCE;

    /** Active player sessions */
    private final Map<UUID, ChunkMapSession> sessions = new java.util.concurrent.ConcurrentHashMap<>();

    private PolyChunkMapServer() {}

    // ── Singleton ─────────────────────────────────────────────────────────────

    public static void init()
    {
        net.creeperhost.polylib.PolyFeatures.enableChunkMap();
        INSTANCE = new PolyChunkMapServer();
    }

    public static @Nullable PolyChunkMapServer getInstance()
    {
        return INSTANCE;
    }

    // ── Permission ────────────────────────────────────────────────────────────

    public boolean isPermitted(ServerPlayer player)
    {
        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        // Single-player / integrated server → always permitted
        if (!server.isDedicatedServer()) return true;
        // Gamerule overrides OP requirement
        if (PolyChunkMapGamerule.isOpenToAll(server)) return true;
        // Dedicated server default: require OP ADMINS (old level 3)
        return isAtLeastAdmin(player);
    }

    /** Returns true if the player has at least {@link PermissionLevel#ADMINS} OP. */
    private static boolean isAtLeastAdmin(ServerPlayer player)
    {
        var perms = player.permissions();
        if (perms instanceof LevelBasedPermissionSet lbps) {
            return lbps.level().isEqualOrHigherThan(PermissionLevel.ADMINS);
        }
        // Fallback: allow if ALL_PERMISSIONS granted
        return perms == PermissionSet.ALL_PERMISSIONS;
    }

    private ChunkMapSession getOrCreateSession(ServerPlayer player) {
        return sessions.computeIfAbsent(player.getUUID(), id -> new ChunkMapSession(player));
    }

    // ── Lifecycle events (called from platform-specific event handlers) ────────

    /** Called when a player joins — sends Hello if permitted. */
    public void onPlayerJoin(ServerPlayer player, MinecraftServer server)
    {
        // Defer one tick so permission plugins (e.g. LuckPerms) finish loading
        server.execute(() -> {
            getOrCreateSession(player).onPlayerJoin();
        });
    }

    /** Called when a player is granted OP. */
    public void onOpPlayer(ServerPlayer player)
    {
        getOrCreateSession(player).checkPermissions();
    }

    /** Called when a player's OP is revoked. */
    public void onDeOpPlayer(ServerPlayer player)
    {
        ChunkMapSession session = sessions.get(player.getUUID());
        if (session != null) {
            session.checkPermissions();
        }
    }
    
    public void onPlayerLeave(ServerPlayer player) {
        sessions.remove(player.getUUID());
    }

    /** Called when a level is unloaded. */
    public void onLevelUnload(ServerLevel level)
    {
        ResourceKey<Level> dim = level.dimension();
        for (ChunkMapSession session : sessions.values()) {
            session.onLevelUnload(dim);
        }
    }

    /**
     * Called at the end of each level tick to flush dirty chunks to watching clients.
     * Must be called on the level's server thread.
     */
    public void onLevelTick(ServerLevel level)
    {
        sessions.values().removeIf(session -> level.getServer().getPlayerList().getPlayer(session.getPlayerId()) == null);

        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker();
        PolyChunkTracker.DirtyChunks dirty = tracker.getDirty();
        if (dirty.updated().isEmpty() && dirty.removed().isEmpty()) return;

        for (ChunkMapSession session : sessions.values()) {
            session.sendTickUpdates(level, dirty);
        }
    }

    // ── Payload handlers (called from network registration) ───────────────────

    public void handleStart(PolyChunkMapStartPayload payload, ServerPlayer player)
    {
        getOrCreateSession(player).startWatching(payload.dimensions());
    }

    public void handleStop(PolyChunkMapStopPayload payload, ServerPlayer player)
    {
        ChunkMapSession session = sessions.get(player.getUUID());
        if (session != null) {
            session.stopWatching(payload.dimensions());
        }
    }

    public void handleRefresh(PolyChunkMapRefreshPayload payload, ServerPlayer player)
    {
        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        // Refresh must run on each level's tick thread; schedule via the server
        for (ServerLevel level : server.getAllLevels()) {
            server.execute(() -> ((PolyChunkTrackerHolder) level).polylib$getChunkTracker().refresh());
        }
    }
}
