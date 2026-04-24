package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLivingEvents#USE_TOTEM}.
 * Fires when a living entity is about to use a Totem of Undying.
 */
@Mixin(LivingEntity.class)
public abstract class FabricUseTotemMixin
{
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void polylib$onCheckTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.USE_TOTEM.invoker().onUseTotem(self, source, ctx);
            if (ctx.isCancelled()) cir.setReturnValue(false);
        }
    }
}
