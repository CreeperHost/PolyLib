package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#MOB_SPLIT}.
 * Fires when a Slime is removed (which triggers splitting into smaller slimes).
 */
@Mixin(Slime.class)
public abstract class FabricMobSplitMixin
{
    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void polylib$onRemove(Entity.RemovalReason reason, CallbackInfo ci)
    {
        if (reason == Entity.RemovalReason.KILLED)
        {
            Slime self = (Slime) (Object) this;
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.MOB_SPLIT.invoker().onMobSplit(self, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
