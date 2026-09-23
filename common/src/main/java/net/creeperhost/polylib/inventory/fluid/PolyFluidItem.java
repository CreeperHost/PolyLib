package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.world.item.ItemStack;

/**
 * Implement on items that expose PolyLib fluid storage.
 */
public interface PolyFluidItem {
    /**
     * Gets a fluid storage view for the supplied stack.
     *
     * @param stack item stack being queried
     * @return item-backed fluid storage
     */
    IPolyFluidStorageItem getFluidStorage(ItemStack stack);
}
