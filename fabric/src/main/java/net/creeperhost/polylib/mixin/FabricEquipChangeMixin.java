package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#ENTITY_EQUIP_CHANGE}.
 */
@Mixin(LivingEntity.class)
public abstract class FabricEquipChangeMixin
{
    @Inject(method = "onEquipItem", at = @At("HEAD"))
    private void polylib$onEquipItem(EquipmentSlot slot, ItemStack from, ItemStack to, CallbackInfo ci)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        PolyLivingEvents.ENTITY_EQUIP_CHANGE.invoker().onEquipChange(self, slot, from, to);
    }
}
