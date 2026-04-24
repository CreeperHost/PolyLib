package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyItemEvents#FUEL_BURN_TIME}.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FabricFuelBurnTimeMixin
{
    //TODO
//    @Inject(method = "getBurnDuration", at = @At("RETURN"), cancellable = true)
//    private void polylib$onGetBurnDuration(net.minecraft.world.item.crafting.RecipeType<?> recipeType, ItemStack stack, net.minecraft.world.level.block.entity.FuelValues fuelValues, CallbackInfoReturnable<Integer> cir)
//    {
//        int[] time = { cir.getReturnValue() };
//        PolyItemEvents.FUEL_BURN_TIME.invoker().onFuelBurnTime(stack, time);
//        cir.setReturnValue(time[0]);
//    }
}
