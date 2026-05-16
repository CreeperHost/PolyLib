package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerReference;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ServerChunkCache mixin: wires the {@link PolyChunkTracker} into the
 * {@link PolyTicketStorageMixin} so ticket events reach the tracker.
 */
@Mixin(ServerChunkCache.class)
public class PolyServerChunkCacheMixin
{
    @Shadow @Final public ChunkMap chunkMap;
    @Shadow @Final ServerLevel level;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void polylib$onInit(CallbackInfo ci)
    {
        PolyChunkTracker tracker = ((PolyChunkTrackerHolder) this.level).polylib$getChunkTracker();
        TicketStorage ticketStorage = ((PolyDistanceManagerAccessor) this.chunkMap.getDistanceManager()).polylib$getTicketStorage();
        ((PolyChunkTrackerReference) ticketStorage).polylib$setTracker(tracker);
    }
}
