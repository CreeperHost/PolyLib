package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyPlayerEvents#ITEM_CRAFTED} on Fabric.
 * Fires when a player takes an item from the crafting result slot.
 */
@Mixin(ResultSlot.class)
public abstract class FabricItemCraftedMixin
{
    @Final @Shadow private CraftingContainer craftSlots;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void polylib$onCraft(Player player, ItemStack stack, CallbackInfo ci)
    {
        if (player instanceof ServerPlayer sp)
            PolyPlayerEvents.ITEM_CRAFTED.invoker().onItemCrafted(sp, stack, craftSlots);
    }
}

