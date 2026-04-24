package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyBlockEvents#CROP_GROW_PRE} and
 * {@link PolyBlockEvents#CROP_GROW_POST}.
 * Fires before and after a crop block grows.
 */
@Mixin(CropBlock.class)
public abstract class FabricCropGrowMixin
{
    //TODO
//    @Inject(method = "grow", at = @At("HEAD"), cancellable = true)
//    private void polylib$onCropGrowPre(Level level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci)
//    {
//        CancelContext ctx = new CancelContext();
//        PolyBlockEvents.CROP_GROW_PRE.invoker().onCropGrow(level, pos, state, ctx);
//        if (ctx.isCancelled()) ci.cancel();
//    }
//
//    @Inject(method = "grow", at = @At("RETURN"))
//    private void polylib$onCropGrowPost(Level level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci)
//    {
//        PolyBlockEvents.CROP_GROW_POST.invoker().onCropGrowPost(level, pos, state);
//    }
}
