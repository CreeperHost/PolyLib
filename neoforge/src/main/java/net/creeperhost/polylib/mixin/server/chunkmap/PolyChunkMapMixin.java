package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * Hooks {@link ChunkMap} to notify the tracker when chunks are scheduled for
 * unloading, fully unloaded, or advance through a generation stage.
 */
@Mixin(ChunkMap.class)
public abstract class PolyChunkMapMixin
{
    @Shadow @Final private ServerLevel level;

    // ── Unloading: mark chunk as unloading ────────────────────────────────────

    @Inject(
            method = "processUnloads",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;scheduleUnload(JLnet/minecraft/server/level/ChunkHolder;)V"
            )
    )
    private void polylib$onScheduleUnload(BooleanSupplier hasMoreTime, CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local(ordinal = 0, type = long.class) long pos)
    {
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker();
        tracker.setUnloading(pos, true);
    }

    // ── Full unload: remove from tracker ──────────────────────────────────────
    // Hooks into the lambda inside scheduleUnload that removes the holder from
    // the visible map.  Target is the Long2ObjectLinkedOpenHashMap.remove call.
    @Inject(
            method = "lambda$scheduleUnload$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;remove(JLjava/lang/Object;)Z"
            )
    )
    private void polylib$onUnloadHolder(ChunkHolder holder, CompletableFuture<?> future, long pos, CallbackInfo ci)
    {
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker();
        tracker.unload(pos);
    }

    // ── Stage change during generation ────────────────────────────────────────

    @Inject(
            method = "applyStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/status/ChunkStep;apply("
                            + "Lnet/minecraft/world/level/chunk/status/WorldGenContext;"
                            + "Lnet/minecraft/util/StaticCache2D;"
                            + "Lnet/minecraft/world/level/chunk/ChunkAccess;)"
                            + "Ljava/util/concurrent/CompletableFuture;",
                    shift = At.Shift.AFTER
            )
    )
    private void polylib$onStageChange(
            GenerationChunkHolder chunk,
            ChunkStep step,
            StaticCache2D<GenerationChunkHolder> cache,
            CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir)
    {
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker();
        ChunkStatus target = step.targetStatus();
        tracker.queueStage(chunk.getPos().pack(), target);
    }
}


