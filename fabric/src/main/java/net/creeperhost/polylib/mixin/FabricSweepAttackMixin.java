package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#SWEEP_ATTACK}.
 * Fires at the sweep-attack visual call inside {@code Player#attack(Entity)}, which
 * occurs after sweep damage has already been applied to nearby entities.
 * <p>
 * Note: the {@code target} parameter is the primary attack target (not each swept entity).
 * Cancellation has no effect on damage; this event is primarily informational on Fabric.
 */
@Mixin(Player.class)
public abstract class FabricSweepAttackMixin
{
    @Inject(method = "attack",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/entity/player/Player;sweepAttack()V"))
    private void polylib$onSweepAttack(Entity target, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.SWEEP_ATTACK.invoker().onSweepAttack((Player) (Object) this, target, ctx);
    }
}
