package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolyEntityEvents#PROJECTILE_IMPACT} on Fabric.
 */
@Mixin(Projectile.class)
public abstract class FabricProjectileImpactMixin
{
    @Inject(method = "hitTargetOrDeflectSelf",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onImpact(HitResult hitResult, CallbackInfoReturnable<ProjectileDeflection> cir)
    {
        Projectile self = (Projectile) (Object) this;
        if (!(self.level() instanceof ServerLevel)) return;
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(self, hitResult, ctx);
        if (ctx.isCancelled()) cir.setReturnValue(ProjectileDeflection.NONE);
    }
}
