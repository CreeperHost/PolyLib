package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolyLivingEvents#ARROW_NOCK} on Fabric.
 */
@Mixin(BowItem.class)
public abstract class FabricArrowNockMixin
{
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void polylib$onBowUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (player instanceof ServerPlayer sp)
        {
            ItemStack bow = player.getItemInHand(hand);
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.ARROW_NOCK.invoker().onArrowNock(sp, bow, hand, ctx);
            if (ctx.isCancelled())
                cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
