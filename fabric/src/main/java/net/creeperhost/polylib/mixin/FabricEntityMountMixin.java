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
 * Fabric bridge for {@link PolyEntityEvents#MOUNT}.
 * Fires when an entity is about to mount a vehicle.
 */
@Mixin(Entity.class)
public abstract class FabricEntityMountMixin
{
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
    private void polylib$onStartRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir)
    {
        Entity self = (Entity) (Object) this;
        if (self.level() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyEntityEvents.MOUNT.invoker().onMount(self, vehicle, true, ctx);
            if (ctx.isCancelled()) cir.setReturnValue(false);
        }
    }
}
