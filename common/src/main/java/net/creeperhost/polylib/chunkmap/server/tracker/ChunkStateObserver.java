package net.creeperhost.polylib.chunkmap.server.tracker;

import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Concurrency seam for chunk state tracking.
 * Safely queues stage updates from worker threads and drains them to the main-thread tracker.
 */
public class ChunkStateObserver {
    private final ConcurrentLinkedQueue<StageUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();

    public void queueStage(long packedPos, ChunkStatus stage) {
        pendingUpdates.add(new StageUpdate(packedPos, stage));
    }

    public void drainTo(PolyChunkTracker tracker) {
        StageUpdate update;
        while ((update = pendingUpdates.poll()) != null) {
            tracker.applyStageUpdate(update.packedPos, update.stage);
        }
    }

    private record StageUpdate(long packedPos, ChunkStatus stage) {}
}
