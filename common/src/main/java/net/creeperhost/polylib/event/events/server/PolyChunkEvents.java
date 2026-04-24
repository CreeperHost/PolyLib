package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public final class PolyChunkEvents
{
    public static final PolyEvent<Load> CHUNK_LOAD = PolyEvent.create(handlers -> (level, chunk) -> handlers.forEach(h -> h.onLoad(level, chunk)));
    public static final PolyEvent<Unload> CHUNK_UNLOAD = PolyEvent.create(handlers -> (level, chunk) -> handlers.forEach(h -> h.onUnload(level, chunk)));

    // ── Tier 10 ───────────────────────────────────────────────────────────────

    /**
     * Fired when a player starts watching (tracking) a chunk.
     * <p>
     * NeoForge: {@code ChunkWatchEvent.Watch}<br>
     * Fabric: mixin on {@code ChunkMap#updateChunkTracking} or {@code PlayerChunkSender}
     */
    public static final PolyEvent<Watch> CHUNK_WATCH = PolyEvent.create(
            handlers -> (player, pos, level) -> handlers.forEach(h -> h.onWatch(player, pos, level)));

    /**
     * Fired when a player stops watching (tracking) a chunk.
     * <p>
     * NeoForge: {@code ChunkWatchEvent.UnWatch}<br>
     * Fabric: mixin on chunk tracking logic
     */
    public static final PolyEvent<UnWatch> CHUNK_UNWATCH = PolyEvent.create(
            handlers -> (player, pos, level) -> handlers.forEach(h -> h.onUnWatch(player, pos, level)));

    private PolyChunkEvents() {}

    @FunctionalInterface
    public interface Load
    {
        void onLoad(ServerLevel level, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface Unload
    {
        void onUnload(ServerLevel level, LevelChunk chunk);
    }

    @FunctionalInterface
    public interface Watch
    {
        void onWatch(ServerPlayer player, ChunkPos pos, ServerLevel level);
    }

    @FunctionalInterface
    public interface UnWatch
    {
        void onUnWatch(ServerPlayer player, ChunkPos pos, ServerLevel level);
    }
}

