package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyItemEvents#ANVIL_UPDATE}.
 * Fires after the anvil computes a repair/rename result.
 */
@Mixin(AnvilMenu.class)
public abstract class FabricAnvilMixin
{
    @Inject(method = "createResult", at = @At("TAIL"))
    private void polylib$onCreateResult(CallbackInfo ci)
    {
        AnvilMenu self = (AnvilMenu) (Object) this;
        ItemStack left = self.getSlot(0).getItem();
        ItemStack right = self.getSlot(1).getItem();
        ItemStack output = self.getSlot(2).getItem();
        if (output.isEmpty()) return;

        String name = output.getHoverName().getString();
        PolyItemEvents.AnvilContext ctx = new PolyItemEvents.AnvilContext(output.copy(), self.getCost(), 0);
        PolyItemEvents.ANVIL_UPDATE.invoker().onAnvilUpdate(left, right, name, ctx);
        // Note: applying ctx changes back requires access to private fields; event is informational on Fabric
    }
}
