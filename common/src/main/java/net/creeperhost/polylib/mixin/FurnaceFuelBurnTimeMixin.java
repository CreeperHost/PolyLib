package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Bridges the component-based furnace fuel calculation on both loaders. */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceFuelBurnTimeMixin {
    @Inject(method = "getBurnDuration", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetBurnDuration(ServerLevel level, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int[] time = {cir.getReturnValue()};
        PolyItemEvents.FUEL_BURN_TIME.invoker().onFuelBurnTime(stack, time);
        cir.setReturnValue(time[0]);
    }
}
