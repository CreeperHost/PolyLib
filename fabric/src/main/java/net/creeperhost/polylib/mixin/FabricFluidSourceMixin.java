package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyBlockEvents#FLUID_SOURCE}.
 * Fires before a flowing fluid spreads to a new position.
 */
@Mixin(FlowingFluid.class)
public abstract class FabricFluidSourceMixin
{
    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    private void polylib$onSpreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.FLUID_SOURCE.invoker().onFluidSource(level, pos, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
