package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric bridge for {@link PolyItemEvents#ITEM_FISHING}.
 * Fires when a fishing hook is retrieved.
 */
@Mixin(FishingHook.class)
public abstract class FabricItemFishingMixin
{
    @Inject(method = "retrieve", at = @At("HEAD"), cancellable = true)
    private void polylib$onRetrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        FishingHook self = (FishingHook) (Object) this;
        Entity owner = self.getOwner();
        if (owner instanceof net.minecraft.world.entity.player.Player player)
        {
            List<ItemStack> drops = new ArrayList<>();
            CancelContext ctx = new CancelContext();
            PolyItemEvents.ITEM_FISHING.invoker().onItemFishing(player, drops, ctx);
            if (ctx.isCancelled())
            {
                cir.setReturnValue(0);
                cir.cancel();
            }
        }
    }
}
