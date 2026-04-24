package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#CHANGE_TARGET}.
 * Fires when a mob is about to change its attack target.
 */
@Mixin(Mob.class)
public abstract class FabricMobChangeTargetMixin
{
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void polylib$onSetTarget(@Nullable LivingEntity newTarget, CallbackInfo ci)
    {
        if (newTarget == null) return;
        Mob self = (Mob) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.CHANGE_TARGET.invoker().onChangeTarget(self, newTarget, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
