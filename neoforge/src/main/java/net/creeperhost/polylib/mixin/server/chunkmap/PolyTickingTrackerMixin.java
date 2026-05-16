package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerReference;
import net.minecraft.server.level.ChunkTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkTracker.class)
public class PolyTickingTrackerMixin implements PolyChunkTrackerReference {
    @Unique private PolyChunkTracker polylib$tracker;

    @Inject(method = "setLevel(JI)V", at = @At("TAIL"))
    private void polylib$onSetLevel(long pos, int level, CallbackInfo ci) {
        if (this.polylib$tracker != null) {
            String className = this.getClass().getName();
            if (className.endsWith("SimulationChunkTracker") || className.endsWith("TickingTracker")) {
                this.polylib$tracker.setTickingStatusLevel(pos, Math.min(level, 33));
            } else if (className.endsWith("LoadingChunkTracker")) {
                this.polylib$tracker.setStatusLevel(pos, Math.min(level, 33));
            }
        }
    }

    @Override
    public void polylib$setTracker(PolyChunkTracker tracker) {
        this.polylib$tracker = tracker;
    }

    @Override
    public PolyChunkTracker polylib$getTracker() {
        return this.polylib$tracker;
    }
}
