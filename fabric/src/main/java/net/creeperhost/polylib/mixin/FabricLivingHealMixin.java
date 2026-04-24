package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#HEAL}.
 * Fires when a living entity is about to be healed.
 */
@Mixin(LivingEntity.class)
public abstract class FabricLivingHealMixin
{
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void polylib$onHeal(float healAmount, CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.HEAL.invoker().onHeal(self, healAmount, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
