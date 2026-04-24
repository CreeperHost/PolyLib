package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#BREAK_SPEED}.
 */
@Mixin(Player.class)
public abstract class FabricBreakSpeedMixin
{
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir)
    {
        Player self = (Player)(Object) this;
        float[] speed = { cir.getReturnValue() };
        PolyPlayerEvents.BREAK_SPEED.invoker().onBreakSpeed(self, state, self.blockPosition(), speed);
        cir.setReturnValue(speed[0]);
    }
}
