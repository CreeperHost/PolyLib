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
 * Fabric bridge for {@link PolyLivingEvents#KNOCKBACK}.
 * Fires when a living entity is about to be knocked back.
 */
@Mixin(LivingEntity.class)
public abstract class FabricLivingKnockbackMixin
{
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void polylib$onKnockback(double strength, double ratioX, double ratioZ, CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.KNOCKBACK.invoker().onKnockback(self, (float) strength, ratioX, ratioZ, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
