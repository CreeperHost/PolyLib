package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.ServerStatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#STAT_AWARD}.
 */
@Mixin(ServerStatsCounter.class)
public abstract class FabricStatAwardMixin
{
    @Shadow private ServerPlayer player;

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    private void polylib$onSetValue(ServerPlayer player, Stat<?> stat, int value, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.STAT_AWARD.invoker().onStatAward(player, stat, value, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
