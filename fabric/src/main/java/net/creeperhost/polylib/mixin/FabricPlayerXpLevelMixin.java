package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyPlayerEvents#XP_LEVEL_CHANGE} on Fabric.
 */
@Mixin(Player.class)
public abstract class FabricPlayerXpLevelMixin
{
    @Inject(method = "giveExperienceLevels", at = @At("HEAD"), cancellable = true)
    private void polylib$onGiveXpLevels(int levels, CallbackInfo ci)
    {
        if ((Object) this instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.XP_LEVEL_CHANGE.invoker().onXpLevelChange(sp, levels, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
