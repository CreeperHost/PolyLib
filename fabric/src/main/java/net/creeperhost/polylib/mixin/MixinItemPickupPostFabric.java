package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#ITEM_PICKUP_POST}.
 * Fires after {@code ItemEntity#playerTouch} returns (item has been picked up).
 */
@Mixin(ItemEntity.class)
public abstract class MixinItemPickupPostFabric
{
    @Inject(method = "playerTouch", at = @At("RETURN"))
    private void polylib$onItemPickupPost(Player player, CallbackInfo ci)
    {
        ItemEntity self = (ItemEntity) (Object) this;
        // At RETURN the item has already been added to inventory; fire with remaining stack.
        PolyPlayerEvents.ITEM_PICKUP_POST.invoker().onItemPickupPost(player, self, self.getItem());
    }
}
