package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLivingEvents#SPAWN_CLUSTER_SIZE}.
 */
@Mixin(Mob.class)
public abstract class FabricSpawnClusterSizeMixin
{
    @Inject(method = "getMaxSpawnClusterSize", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetMaxSpawnClusterSize(CallbackInfoReturnable<Integer> cir)
    {
        int[] size = { cir.getReturnValue() };
        PolyLivingEvents.SPAWN_CLUSTER_SIZE.invoker().onSpawnClusterSize((Mob)(Object) this, size);
        cir.setReturnValue(size[0]);
    }
}
