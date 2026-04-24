package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#TRADE_WITH_VILLAGER}.
 */
@Mixin(AbstractVillager.class)
public abstract class FabricTradeWithVillagerMixin
{
    @Inject(method = "notifyTrade", at = @At("HEAD"))
    private void polylib$onNotifyTrade(MerchantOffer offer, CallbackInfo ci)
    {
        AbstractVillager self = (AbstractVillager)(Object) this;
        net.minecraft.world.entity.player.Player tradingPlayer = self.getTradingPlayer();
        if (tradingPlayer != null)
        {
            PolyPlayerEvents.TRADE_WITH_VILLAGER.invoker().onTradeWithVillager(tradingPlayer, offer, self);
        }
    }
}
