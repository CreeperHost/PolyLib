package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#ENTITY_KILLED_OTHER}.
 * Fires when a living entity is killed by another living entity.
 */
@Mixin(LivingEntity.class)
public abstract class FabricEntityKilledOtherMixin
{
    @Inject(method = "die", at = @At("HEAD"))
    private void polylib$onDie(DamageSource source, CallbackInfo ci)
    {
        LivingEntity victim = (LivingEntity)(Object) this;
        if (source.getEntity() instanceof LivingEntity killer)
        {
            PolyLivingEvents.ENTITY_KILLED_OTHER.invoker().onEntityKilledOther(killer, victim);
        }
    }
}
