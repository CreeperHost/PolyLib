package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyEntityEvents#STRUCK_BY_LIGHTNING} on Fabric.
 */
@Mixin(Entity.class)
public abstract class FabricEntityLightningMixin
{
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void polylib$onThunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo ci)
    {
        Entity self = (Entity) (Object) this;
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.STRUCK_BY_LIGHTNING.invoker().onStruckByLightning(self, lightning, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
