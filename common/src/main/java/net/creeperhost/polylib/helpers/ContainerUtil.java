package net.creeperhost.polylib.helpers;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 07/03/2024
 */
public class ContainerUtil {


    /**
     * Tries to insert the given stack into the specified container wherever there is room.
     * Will first try to combine with any existing stacks of the same type if possible.
     *
     * @param stack     The itemstack to be inserted (Will be copied before insertion)
     * @param container The container to insert into.
     * @return the number of items that could NOT be inserted.
     */
    public static int insertStack(ItemStack stack, Container container) {
        return insertStack(stack, container, false);
    }

    /**
     * Tries to insert the given stack into the specified container wherever there is room.
     * Will first try to combine with any existing stacks of the same type if possible.
     *
     * @param stack     The itemstack to be inserted (Will be copied before insertion)
     * @param container The container to insert into.
     * @param simulate  Allows you to simulate the insertion without actually inserting any items.
     * @return the number of items that could NOT be inserted.
     */
    public static int insertStack(ItemStack stack, Container container, boolean simulate) {
        ItemStack copy = stack.copy();

        //First check if we can combine with any existing stacks in the inventory
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack inSlot = container.getItem(slot);
            if (inSlot.isEmpty() || !canCombine(copy, inSlot, container.getMaxStackSize()) || !container.canPlaceItem(slot, copy)) {
                continue;
            }
            int add = Math.min(copy.getCount(), inSlot.getMaxStackSize() - inSlot.getCount());
            if (!simulate) {
                inSlot.grow(add);
                container.setItem(slot, inSlot);
            }
            copy.shrink(add);
            if (copy.isEmpty()) {
                return 0;
            }
        }
        //Next check for empty slots we can add to.
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty() && container.canPlaceItem(slot, copy)) {
                if (!simulate) {
                    container.setItem(slot, copy);
                }
                return 0;
            }
        }

        return copy.getCount();
    }

    public static boolean canCombine(ItemStack stack, ItemStack combineWith) {
        return canCombine(stack, combineWith, 64);
    }

    public static boolean canCombine(ItemStack stack, ItemStack combineWith, int maxStackSize) {
        return !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, combineWith) && stack.isStackable() && combineWith.getCount() < combineWith.getMaxStackSize() && combineWith.getCount() < maxStackSize;
    }
}
