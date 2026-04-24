package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyPlayerEvents#SET_SPAWN} on Fabric.
 */
@Mixin(ServerPlayer.class)
public abstract class FabricPlayerSetSpawnMixin
{
    @Inject(method = "setRespawnPosition", at = @At("HEAD"), cancellable = true)
    private void polylib$onSetRespawnPosition(ResourceKey<Level> dimension, BlockPos pos,
                                               float angle, boolean forced, boolean sendMessage,
                                               CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.SET_SPAWN.invoker().onSetSpawn(
                (ServerPlayer) (Object) this, pos, dimension, forced, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
