package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#ITEM_USE_START} and {@link PolyItemEvents#ITEM_USE_START}.
 */
@Mixin(LivingEntity.class)
public abstract class FabricItemUseStartMixin
{
    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void polylib$onStartUsingItem(InteractionHand hand, CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack item = self.getItemInHand(hand);
        int duration = item.getUseDuration(self);
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.ITEM_USE_START.invoker().onItemUseStart(self, item, ctx);
        PolyItemEvents.ITEM_USE_START.invoker().onUseStart(self, item, duration, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
