package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyEnchantEvents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEnchantEvents#ENCHANT_TABLE_LEVEL}.
 * Fires after {@code EnchantmentMenu.slotsChanged} computes the three slot costs,
 * allowing modification of each offered enchantment level.
 * <p>
 * The {@code power} argument is always 0 on Fabric because bookshelf count is not
 * accessible without additional shadowing; NeoForge provides the exact power value.
 */
@Mixin(EnchantmentMenu.class)
public abstract class FabricEnchantTableLevelMixin
{
    @Final @Shadow private Container enchantSlots;
    @Shadow public final int[] costs = null;

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void polylib$onSlotsChanged(Container container, CallbackInfo ci)
    {
        ItemStack stack = this.enchantSlots.getItem(0);
        for (int i = 0; i < 3; i++)
        {
            if (this.costs[i] > 0)
            {
                int[] level = { this.costs[i] };
                PolyEnchantEvents.ENCHANT_TABLE_LEVEL.invoker().onEnchantTableLevel(stack, 0, this.costs[i], level);
                this.costs[i] = level[0];
            }
        }
    }
}
