package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyBlockEvents#PISTON_PRE}.
 * Fires when a piston is triggered (TRIGGER_EXTEND = 0, TRIGGER_CONTRACT = 1).
 */
@Mixin(PistonBaseBlock.class)
public abstract class FabricPistonPreMixin
{
    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void polylib$onTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir)
    {
        if (type == PistonBaseBlock.TRIGGER_EXTEND || type == PistonBaseBlock.TRIGGER_CONTRACT)
        {
            boolean extending = type == PistonBaseBlock.TRIGGER_EXTEND;
            Direction direction = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.PISTON_PRE.invoker().onPistonPre(level, pos, direction, extending, ctx);
            if (ctx.isCancelled())
            {
                cir.setReturnValue(false);
            }
        }
    }
}
