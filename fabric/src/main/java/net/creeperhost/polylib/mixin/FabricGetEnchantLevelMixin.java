package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyEnchantEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class FabricGetEnchantLevelMixin
{
    //TODO
//    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
//    private static void polylib$onGetItemEnchantmentLevel(
//            Holder<Enchantment> enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir)
//    {
//        ItemEnchantments base = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
//        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(base);
//        PolyEnchantEvents.GET_ENCHANT_LEVEL.invoker().onGetEnchantLevel(stack, enchantment, mutable);
//        int newLevel = mutable.getLevel(enchantment);
//        if (newLevel != cir.getReturnValueI())
//        {
//            cir.setReturnValue(newLevel);
//        }
//    }
}
