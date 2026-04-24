package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyChunkEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyChunkEvents#CHUNK_WATCH}.
 */
@Mixin(ChunkMap.class)
public abstract class FabricChunkWatchMixin
{
    //TODO
//    @Final @Shadow ServerLevel level;
//
//    @Inject(method = "playerLoadedChunk", at = @At("HEAD"))
//    private void polylib$onPlayerLoadedChunk(ServerPlayer player, net.minecraft.network.protocol.Packet<?>[] packets, LevelChunk chunk, CallbackInfo ci)
//    {
//        PolyChunkEvents.CHUNK_WATCH.invoker().onWatch(player, chunk.getPos(), level);
//    }
}
