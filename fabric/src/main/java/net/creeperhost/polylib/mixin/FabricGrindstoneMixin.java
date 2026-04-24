package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyItemEvents#GRINDSTONE}.
 * Fires after the grindstone slots change and a result is computed.
 */
@Mixin(GrindstoneMenu.class)
public abstract class FabricGrindstoneMixin
{
    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void polylib$onSlotsChanged(net.minecraft.world.Container container, CallbackInfo ci)
    {
        GrindstoneMenu self = (GrindstoneMenu) (Object) this;
        ItemStack topItem = self.getSlot(GrindstoneMenu.INPUT_SLOT).getItem();
        ItemStack bottomItem = self.getSlot(GrindstoneMenu.ADDITIONAL_SLOT).getItem();
        ItemStack output = self.getSlot(GrindstoneMenu.RESULT_SLOT).getItem();
        if (output.isEmpty()) return;

        PolyItemEvents.GrindstoneContext ctx = new PolyItemEvents.GrindstoneContext(
                topItem, bottomItem, output.copy(), 0);
        PolyItemEvents.GRINDSTONE.invoker().onGrindstone(topItem, bottomItem, ctx);
        // Note: setting the result slot back is complex from a mixin; fire event informational here
    }
}
