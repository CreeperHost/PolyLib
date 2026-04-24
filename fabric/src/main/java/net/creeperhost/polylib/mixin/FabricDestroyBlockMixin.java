package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#DESTROY_BLOCK}.
 * Fires when an Enderman destroys (picks up) a block.
 */
@Mixin(EnderMan.class)
public abstract class FabricDestroyBlockMixin
{
    //TODO
//    @Inject(method = "pickUpBlock", at = @At("HEAD"), cancellable = true)
//    private void polylib$onPickUpBlock(net.minecraft.server.level.ServerLevel level, BlockPos pos, BlockState state, CallbackInfo ci)
//    {
//        EnderMan self = (EnderMan) (Object) this;
//        CancelContext ctx = new CancelContext();
//        PolyLivingEvents.DESTROY_BLOCK.invoker().onDestroyBlock(self, level, pos, state, ctx);
//        if (ctx.isCancelled()) ci.cancel();
//    }
}
