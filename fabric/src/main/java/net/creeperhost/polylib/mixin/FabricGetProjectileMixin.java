package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLivingEvents#GET_PROJECTILE}.
 * Fires when a living entity determines which projectile to use.
 */
@Mixin(LivingEntity.class)
public abstract class FabricGetProjectileMixin
{
    @Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetProjectile(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack[] resultHolder = { cir.getReturnValue() };
        PolyLivingEvents.GET_PROJECTILE.invoker().onGetProjectile(self, resultHolder);
        if (resultHolder[0] != cir.getReturnValue())
        {
            cir.setReturnValue(resultHolder[0]);
        }
    }
}
