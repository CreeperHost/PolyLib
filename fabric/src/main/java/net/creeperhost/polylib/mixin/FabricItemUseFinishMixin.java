package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#ITEM_USE_FINISH} and {@link PolyItemEvents#ITEM_USE_FINISH}.
 * Note: result modification is best-effort; the result is computed inline.
 */
@Mixin(LivingEntity.class)
public abstract class FabricItemUseFinishMixin
{
    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void polylib$onCompleteUsingItem(CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack usingItem = self.getUseItem();
        ItemStack[] resultHolder = { usingItem.copy() };
        PolyLivingEvents.ITEM_USE_FINISH.invoker().onItemUseFinish(self, usingItem, resultHolder);
        PolyItemEvents.ITEM_USE_FINISH.invoker().onUseFinish(self, usingItem);
        // Result override not trivially injectable — informational use only on Fabric
    }
}
