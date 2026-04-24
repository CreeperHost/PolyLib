package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientChunkEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientChunkEvents#CLIENT_CHUNK_LOAD}.
 * Fires after {@code ClientLevel#onChunkLoaded} has processed the chunk.
 */
@Mixin(ClientLevel.class)
public abstract class MixinClientChunkLoadNF
{
    @Inject(method = "onChunkLoaded", at = @At("RETURN"))
    private void polylib$onChunkLoaded(ChunkPos pos, CallbackInfo ci)
    {
        ClientLevel self = (ClientLevel) (Object) this;
        LevelChunk chunk = self.getChunkSource().getChunk(pos.getMinBlockX() >> 4, pos.getMinBlockZ() >> 4, false);
        if (chunk != null)
        {
            PolyClientChunkEvents.CLIENT_CHUNK_LOAD.invoker().onChunkLoad(self, chunk);
        }
    }
}
