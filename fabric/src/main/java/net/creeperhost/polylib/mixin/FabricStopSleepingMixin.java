package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolySleepEvents#STOP_SLEEPING} on Fabric (and NeoForge via its own mixin config).
 */
@Mixin(LivingEntity.class)
public abstract class FabricStopSleepingMixin
{
    @Inject(method = "stopSleeping", at = @At("TAIL"))
    private void polylib$onStopSleeping(CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        BlockPos pos = self.getSleepingPos().orElse(BlockPos.ZERO);
        if (self.level() instanceof ServerLevel)
            PolySleepEvents.STOP_SLEEPING.invoker().onStopSleeping(self, pos);
    }
}
