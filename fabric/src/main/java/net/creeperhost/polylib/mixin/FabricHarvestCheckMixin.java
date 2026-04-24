package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#HARVEST_CHECK}.
 */
@Mixin(Player.class)
public abstract class FabricHarvestCheckMixin
{
    @Inject(method = "hasCorrectToolForDrops", at = @At("RETURN"), cancellable = true)
    private void polylib$onHasCorrectToolForDrops(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        Player self = (Player)(Object) this;
        boolean[] result = { cir.getReturnValue() };
        PolyPlayerEvents.HARVEST_CHECK.invoker().onHarvestCheck(self, state, result);
        cir.setReturnValue(result[0]);
    }
}
