package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * ChunkHolder mixin: fires {@link PolyChunkTracker#set} when a holder's tick-futures
 * are updated — this is the main "chunk state changed" event.
 */
@Mixin(ChunkHolder.class)
public abstract class PolyChunkHolderMixin extends GenerationChunkHolder
{
    @Shadow public abstract int getTicketLevel();

    protected PolyChunkHolderMixin(ChunkPos pos) { super(pos); }

    @Inject(method = "updateFutures", at = @At("RETURN"))
    private void polylib$onUpdateFutures(ChunkMap chunkMap, Executor executor, CallbackInfo ci)
    {
        if (!net.creeperhost.polylib.PolyFeatures.isChunkMapEnabled()) return;
        ServerLevel level = ((PolyChunkMapAccessor) chunkMap).polylib$getLevel();
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker();
        DistanceManager dm = chunkMap.getDistanceManager();

        List<Ticket> tickets = ((PolyDistanceManagerAccessor) dm).polylib$getTicketStorage().getTickets(this.pos.pack());
        int tickingLevel = ((PolyDistanceManagerAccessor) dm).polylib$getSimulationTracker().getLevel(this.pos);
        ChunkStatus stage = this.getPersistedStatus();

        tracker.set(this.pos, stage, tickets, this.getTicketLevel(), tickingLevel, false);
    }
}
