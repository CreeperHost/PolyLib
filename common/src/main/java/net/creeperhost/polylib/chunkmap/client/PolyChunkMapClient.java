package net.creeperhost.polylib.chunkmap.client;

import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.chunkmap.common.network.PolyChunkMapDataPayload;
import net.creeperhost.polylib.chunkmap.common.network.PolyChunkMapUnloadPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side state hub for the PolyLib chunk-map system.
 *
 * <p>All methods must be called on the <em>client</em> main thread.
 *
 * <ul>
 *   <li>{@link #onHello()} — server granted access, unlock the map button / keybind.</li>
 *   <li>{@link #onBye()} — server revoked access, close any open map screen.</li>
 *   <li>{@link #onData(PolyChunkMapDataPayload)} — merge incoming chunk data into the local cache.</li>
 *   <li>{@link #onUnload(PolyChunkMapUnloadPayload)} — remove chunks from the cache.</li>
 * </ul>
 *
 * <p>Downstream mods (e.g. DCH) obtain a read-only snapshot via {@link #getChunks(ResourceKey)}.
 */
public final class PolyChunkMapClient
{
    private static final Logger LOG = LogManager.getLogger("PolyChunkMapClient");

    /** Whether the server has granted permission to use the chunk-map. */
    private static volatile boolean permitted = false;

    /**
     * Per-dimension chunk data cache.  Key = packed ChunkPos, value = latest snapshot.
     *
     * <p>Uses {@link ConcurrentHashMap} so the renderer thread can safely read while the
     * network thread writes (though we always flush onto the main thread via enqueueWork).
     */
    private static final Map<ResourceKey<Level>, Map<Long, PolyChunkMapData>> CHUNKS =
            new ConcurrentHashMap<>();

    private PolyChunkMapClient() {}

    // ── Permission state ──────────────────────────────────────────────────────

    public static void onHello()
    {
        permitted = true;
        LOG.debug("[PolyChunkMap] Access granted by server.");
    }

    public static void onBye()
    {
        permitted = false;
        CHUNKS.clear();
        LOG.debug("[PolyChunkMap] Access revoked by server — cache cleared.");
        // TODO: close PolyChunkMapScreen if open
    }

    public static boolean isPermitted()
    {
        return permitted;
    }

    // ── Data handling ─────────────────────────────────────────────────────────

    public static void onData(PolyChunkMapDataPayload payload)
    {
        if (!permitted) return;

        Map<Long, PolyChunkMapData> dim = CHUNKS.computeIfAbsent(payload.dimension(),
                k -> new ConcurrentHashMap<>());

        for (PolyChunkMapData entry : payload.chunks())
        {
            dim.put(entry.position().pack(), entry);
        }
    }

    public static void onUnload(PolyChunkMapUnloadPayload payload)
    {
        Map<Long, PolyChunkMapData> dim = CHUNKS.get(payload.dimension());
        if (dim == null) return;

        long currentTick = net.minecraft.client.Minecraft.getInstance().level != null ? net.minecraft.client.Minecraft.getInstance().level.getGameTime() : 0;

        for (long pos : payload.positions())
        {
            PolyChunkMapData data = dim.remove(pos);
            if (data != null) {
                net.creeperhost.polylib.chunkmap.client.PolyChunkGridRenderer.fadingChunks.put(pos, new net.creeperhost.polylib.chunkmap.client.PolyChunkGridRenderer.FadingChunk(data, currentTick));
            }
        }
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /**
     * Returns an unmodifiable snapshot of all known chunks for the given dimension,
     * or an empty map if no data has been received yet.
     *
     * <p>Safe to call from the render thread — backed by {@link ConcurrentHashMap}.
     */
    public static Map<Long, PolyChunkMapData> getChunks(ResourceKey<Level> dimension)
    {
        return Map.copyOf(CHUNKS.getOrDefault(dimension, Map.of()));
    }

    /**
     * Returns all currently tracked dimensions.
     */
    public static java.util.Set<ResourceKey<Level>> getDimensions()
    {
        return java.util.Collections.unmodifiableSet(CHUNKS.keySet());
    }

    /**
     * Clears all client-side state — called on disconnect.
     */
    public static void reset()
    {
        permitted = false;
        CHUNKS.clear();
    }
}
