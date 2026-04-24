package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolyLivingEvents#SHIELD_BLOCK} on Fabric.
 * <p>
 * Limitation: the exact {@code damageBlocked} float is unavailable at this injection point;
 * {@code 0f} is passed as a placeholder.
 */
@Mixin(LivingEntity.class)
public abstract class FabricShieldBlockMixin
{
    //TODO
//    @Inject(method = "isDamageSourceBlocked",
//            at = @At("RETURN"), cancellable = true)
//    private void polylib$onShieldBlock(DamageSource source, CallbackInfoReturnable<Boolean> cir)
//    {
//        if (!cir.getReturnValue()) return; // Only intercept when vanilla says "blocked"
//        LivingEntity self = (LivingEntity) (Object) this;
//        if (!(self.level() instanceof ServerLevel)) return;
//        CancelContext ctx = new CancelContext();
//        PolyLivingEvents.SHIELD_BLOCK.invoker().onShieldBlock(self, source, 0f, ctx);
//        if (ctx.isCancelled()) cir.setReturnValue(false);
//    }
}
