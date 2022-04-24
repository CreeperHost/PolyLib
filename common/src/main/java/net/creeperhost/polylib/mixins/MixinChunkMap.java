package net.creeperhost.polylib.mixins;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkMap.class)
public class MixinChunkMap
{
    @Inject(method = "protoChunkToFullChunk", at = @At(value = "RETURN", target = "Lnet/minecraft/world/level/chunk/LevelChunk;registerTickContainerInLevel(Lnet/minecraft/server/level/ServerLevel;)V", opcode = Opcodes.PUTFIELD, ordinal = 0), cancellable = true)
    public void protoChunkToFullChunk(ChunkHolder chunkHolder, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir)
    {

    }
}
