package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyItemEvents#ITEM_USE_TICK}.
 * Fires each tick while a living entity is actively using an item.
 */
@Mixin(LivingEntity.class)
public abstract class FabricItemUseTickMixin
{
    @Shadow
    protected int useItemRemaining;

    @Inject(method = "updateUsingItem", at = @At("HEAD"), cancellable = true)
    private void polylib$onUpdateUsingItem(ItemStack usingItem, CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        CancelContext ctx = new CancelContext();
        PolyItemEvents.ITEM_USE_TICK.invoker().onUseTick(self, usingItem, useItemRemaining, ctx);
        if (ctx.isCancelled())
        {
            self.releaseUsingItem();
            ci.cancel();
        }
    }
}
