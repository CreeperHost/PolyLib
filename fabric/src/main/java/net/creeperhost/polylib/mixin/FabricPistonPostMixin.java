package net.creeperhost.polylib.mixin;

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
 * Fabric bridge for {@link PolyBlockEvents#PISTON_POST}.
 * Fires after a piston trigger completes (extend or retract).
 */
@Mixin(PistonBaseBlock.class)
public abstract class FabricPistonPostMixin
{
    @Inject(method = "triggerEvent", at = @At("RETURN"))
    private void polylib$onTriggerEventReturn(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue() && (type == PistonBaseBlock.TRIGGER_EXTEND || type == PistonBaseBlock.TRIGGER_CONTRACT))
        {
            boolean extending = type == PistonBaseBlock.TRIGGER_EXTEND;
            Direction direction = state.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
            PolyBlockEvents.PISTON_POST.invoker().onPistonPost(level, pos, direction, extending);
        }
    }
}
