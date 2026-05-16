package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerReference;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTracker;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(DistanceManager.class)
public abstract class PolyDistanceManagerMixin implements PolyChunkTrackerHolder {
    @Shadow @Final private ChunkTracker simulationChunkTracker;
    @Shadow @Final private ChunkTracker loadingChunkTracker;
    @Shadow @Final private TicketStorage ticketStorage;

    @Shadow @Final ChunkMap this$0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void polylib$onInit(Executor executor, Executor executor2, CallbackInfo ci) {
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) ((PolyChunkMapAccessor) this.this$0).polylib$getLevel()).polylib$getChunkTracker();
        ((PolyChunkTrackerReference) this.simulationChunkTracker).polylib$setTracker(tracker);
        ((PolyChunkTrackerReference) this.loadingChunkTracker).polylib$setTracker(tracker);
        ((PolyChunkTrackerReference) this.ticketStorage).polylib$setTracker(tracker);
    }
}
