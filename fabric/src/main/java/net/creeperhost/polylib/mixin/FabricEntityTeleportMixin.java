package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolyEntityEvents#TELEPORT} on Fabric by injecting into
 * {@code Entity#teleportTo(ServerLevel, double, double, double, java.util.Set, float, float, boolean)}.
 * <p>
 * Limitation: Position mutation (redirecting the target coordinates) is not propagated
 * from the CancelContext back to the method args in this approach — cancellation only.
 */
@Mixin(Entity.class)
public abstract class FabricEntityTeleportMixin
{
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FFZ)Z",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onTeleportTo(ServerLevel level, double x, double y, double z,
                                       java.util.Set<?> relativeArguments, float yaw, float pitch,
                                       boolean particleEffects,
                                       CallbackInfoReturnable<Boolean> cir)
    {
        Entity self = (Entity) (Object) this;
        if (!(self.level() instanceof ServerLevel)) return;
        double[] target = { x, y, z };
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.TELEPORT.invoker().onTeleport(self, target, ctx);
        if (ctx.isCancelled()) cir.setReturnValue(false);
    }
}
