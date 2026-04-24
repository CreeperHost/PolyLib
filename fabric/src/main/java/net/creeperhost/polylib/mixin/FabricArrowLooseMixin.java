package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyLivingEvents#ARROW_LOOSE} on Fabric.
 */
@Mixin(BowItem.class)
public abstract class FabricArrowLooseMixin
{
    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void polylib$onReleaseUsing(ItemStack stack, Level level, LivingEntity entity,
                                         int timeLeft, CallbackInfo ci)
    {
        if (entity instanceof ServerPlayer sp)
        {
            int charge = BowItem.MAX_DRAW_DURATION - timeLeft;
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.ARROW_LOOSE.invoker().onArrowLoose(sp, stack, charge, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
