package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolySleepEvents#START_SLEEPING} on Fabric (and NeoForge via its own mixin config).
 */
@Mixin(LivingEntity.class)
public abstract class FabricStartSleepingMixin
{
    @Inject(method = "startSleeping", at = @At("RETURN"))
    private void polylib$onStartSleeping(BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValue()) return;
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel)
            PolySleepEvents.START_SLEEPING.invoker().onStartSleeping(self, pos);
    }
}
