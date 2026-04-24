package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLivingEvents#FALL}.
 * Fires when a living entity is about to take fall damage.
 */
@Mixin(LivingEntity.class)
public abstract class FabricLivingEntityFallMixin
{
    @Inject(method = "causeFallDamage", at = @At("HEAD"))
    private void polylib$onCauseFallDamage(double fallDistance, float multiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            PolyLivingEvents.FALL.invoker().onFall(self, fallDistance, multiplier);
        }
    }
}
