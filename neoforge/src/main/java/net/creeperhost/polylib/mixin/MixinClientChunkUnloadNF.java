package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientChunkEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientChunkEvents#CLIENT_CHUNK_UNLOAD}.
 * Fires before {@code ClientLevel#unload(LevelChunk)} removes the chunk so the instance is still valid.
 */
@Mixin(ClientLevel.class)
public abstract class MixinClientChunkUnloadNF
{
    @Inject(method = "unload", at = @At("HEAD"))
    private void polylib$onChunkUnload(LevelChunk chunk, CallbackInfo ci)
    {
        PolyClientChunkEvents.CLIENT_CHUNK_UNLOAD.invoker().onChunkUnload((ClientLevel) (Object) this, chunk);
    }
}
