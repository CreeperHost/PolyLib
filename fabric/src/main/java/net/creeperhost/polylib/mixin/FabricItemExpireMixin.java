package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyItemEvents#ITEM_EXPIRE}.
 * Fires when an ItemEntity's age exceeds its lifespan.
 */
@Mixin(ItemEntity.class)
public abstract class FabricItemExpireMixin
{
    @Shadow private int age;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V", ordinal = 0))
    private void polylib$onExpire(CallbackInfo ci)
    {
        ItemEntity self = (ItemEntity) (Object) this;
        PolyItemEvents.ITEM_EXPIRE.invoker().onItemExpire(self);
    }
}
