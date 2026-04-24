package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#ADVANCEMENT_EARN}.
 */
@Mixin(PlayerAdvancements.class)
public abstract class FabricAdvancementEarnMixin
{
    @Shadow private net.minecraft.server.level.ServerPlayer player;

    @Inject(method = "award", at = @At("RETURN"))
    private void polylib$onAward(AdvancementHolder advancement, String criterion, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue() && advancement.value().rewards() != net.minecraft.advancements.AdvancementRewards.EMPTY)
        {
            // A full advancement was earned (rewards granted = advancement complete)
            PolyPlayerEvents.ADVANCEMENT_EARN.invoker().onAdvancementEarn(player, advancement);
        }
    }
}
