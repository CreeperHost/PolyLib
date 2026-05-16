package net.creeperhost.polylib.chunkmap.server.tracker;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkTicket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Per-{@link ServerLevel} chunk state tracker.
 *
 * <p>Maintains a mutable snapshot of every loaded chunk's state and a dirty set
 * of chunks whose state has changed since the last network flush.  Queried each
 * tick by {@link net.creeperhost.polylib.chunkmap.server.PolyChunkMapServer} to
 * build update payloads for watching clients.
 *
 * <p><b>Thread safety:</b> {@code stages} uses a synchronized block because
 * chunk-status updates arrive from a worker thread.  All other access must be
 * on the level's main thread.
 */
public class PolyChunkTracker
{
    /** Mutable per-chunk state keyed by packed {@link ChunkPos}. */
    private final Map<Long, MutableState> chunks = new Object2ObjectOpenHashMap<>();

    /** Chunks modified since the last {@link #getDirty()} call. */
    private final LongSet dirty = new LongOpenHashSet();

    /** Concurrency seam for off-thread chunk generation updates. */
    private final ChunkStateObserver observer = new ChunkStateObserver();

    private final ServerLevel level;

    public PolyChunkTracker(ServerLevel level)
    {
        this.level = level;
    }

    // ── Query API (called on level thread) ────────────────────────────────────

    /** Returns immutable snapshots of all currently loaded chunks. */
    public Collection<PolyChunkMapData> getAll()
    {
        checkThread();
        List<PolyChunkMapData> result = new ArrayList<>(chunks.size());
        for (MutableState s : chunks.values()) result.add(s.snapshot());
        return result;
    }

    /**
     * Drains and returns the dirty set, partitioned into updated and removed chunks.
     * Called once per tick before sending network updates.
     */
    public DirtyChunks getDirty()
    {
        checkThread();
        List<PolyChunkMapData> updated = new ArrayList<>(dirty.size());
        LongList removed = new LongArrayList();
        LongIterator iter = dirty.iterator();
        while (iter.hasNext()) {
            long pos = iter.nextLong();
            MutableState state = chunks.get(pos);
            if (state != null) {
                updated.add(state.snapshot());
            } else {
                removed.add(pos);
            }
        }
        dirty.clear();
        return new DirtyChunks(updated, removed);
    }

    // ── Mutation (called on level thread) ─────────────────────────────────────

    /** Called each tick to flush pending off-thread stage updates. */
    public void tick()
    {
        checkThread();
        observer.drainTo(this);
    }

    /** Called by ChunkStateObserver to apply stage changes on the main thread. */
    public void applyStageUpdate(long packed, ChunkStatus stage)
    {
        checkThread();
        MutableState s = chunks.get(packed);
        if (s != null && s.stage != stage) {
            s.stage = stage;
            markDirty(packed);
        }
    }

    /**
     * Forcibly clears all tracked state.  Triggered by a client refresh request.
     * The server will then send a fresh full-sync burst.
     */
    public void refresh()
    {
        checkThread();
        chunks.clear();
        dirty.clear();
        // Any pending items in the observer will just drop during drain since chunks is empty.
    }

    /** Add or update a chunk. */
    public void set(ChunkPos pos, @Nullable ChunkStatus stage, List<Ticket> tickets,
                    int statusLevel, int tickingStatusLevel, boolean unloading)
    {
        long packed = pos.pack();
        if (markDirty(packed)) {
            List<PolyChunkTicket> poly = convertTickets(tickets);
            chunks.put(packed, new MutableState(pos, stage, poly, statusLevel, tickingStatusLevel, unloading));
        }
    }

    /** Remove a chunk (it has been fully unloaded). */
    public void unload(long packed)
    {
        if (markDirty(packed)) chunks.remove(packed);
    }

    /** Update the unloading flag for an existing chunk. */
    public void setUnloading(long packed, boolean unloading)
    {
        MutableState s = chunks.get(packed);
        if (s != null) { s.unloading = unloading; markDirty(packed); }
    }

    /** Update tickets for an existing chunk (called from TicketStorage mixin). */
    public void setTickets(long packed, List<Ticket> tickets)
    {
        MutableState s = chunks.get(packed);
        if (s != null) { s.tickets = convertTickets(tickets); markDirty(packed); }
    }

    /** Update the ticking status level (off-main-thread; synchronized via markDirty). */
    public void setTickingStatusLevel(long packed, int level)
    {
        MutableState s = chunks.get(packed);
        if (s != null) { s.tickingStatusLevel = level; markDirty(packed); }
    }

    /** Update the status level. */
    public void setStatusLevel(long packed, int level)
    {
        MutableState s = chunks.get(packed);
        if (s != null) { s.statusLevel = level; markDirty(packed); }
    }

    public void queueStage(long packed, ChunkStatus stage)
    {
        observer.queueStage(packed, stage);
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private boolean markDirty(long packed)
    {
        if (!level.getServer().isStopped()) {
            checkThread();
            dirty.add(packed);
            return true;
        }
        return false;
    }

    private void checkThread()
    {
        if (!level.getServer().isSameThread()) {
            throw new IllegalStateException("PolyChunkTracker accessed off level thread");
        }
    }

    private static List<PolyChunkTicket> convertTickets(List<Ticket> tickets)
    {
        PolyChunkTicket[] result = new PolyChunkTicket[tickets.size()];
        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            result[i] = new PolyChunkTicket(t.getType(), t.getTicketLevel(), 0);
        }
        return ImmutableList.copyOf(result);
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    /** Mutable per-chunk state (server-side only, never serialised directly). */
    private static final class MutableState
    {
        final ChunkPos position;
        @Nullable ChunkStatus stage;
        List<PolyChunkTicket> tickets;
        int statusLevel;
        int tickingStatusLevel;
        boolean unloading;

        MutableState(ChunkPos position, @Nullable ChunkStatus stage,
                     List<PolyChunkTicket> tickets, int statusLevel,
                     int tickingStatusLevel, boolean unloading)
        {
            this.position = position;
            this.stage = stage;
            this.tickets = tickets;
            this.statusLevel = statusLevel;
            this.tickingStatusLevel = tickingStatusLevel;
            this.unloading = unloading;
        }

        PolyChunkMapData snapshot()
        {
            return new PolyChunkMapData(position, stage,
                    ImmutableList.copyOf(tickets),
                    statusLevel, tickingStatusLevel, unloading);
        }
    }

    /** Snapshot of dirty chunks partitioned into updated and removed. */
    public record DirtyChunks(List<PolyChunkMapData> updated, LongList removed) {}
}
