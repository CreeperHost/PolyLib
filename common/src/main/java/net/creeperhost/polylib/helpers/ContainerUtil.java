package net.creeperhost.polylib.helpers;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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
        return !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, combineWith) && stack.isStackable() && combineWith.getCount() < combineWith.getMaxStackSize() && combineWith.getCount() < maxStackSize;
    }

    //The following methods are adapted from Forge's InvWrapper

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     *
     * @param container The container we are inserted into.
     * @param slot      Slot to insert into.
     * @param stack     ItemStack to insert. This must not be modified by the item handler.
     * @param simulate  If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     **/
    @NotNull
    public static ItemStack insertItem(Container container, int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = container.getItem(slot);
        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), container.getMaxStackSize())) {
                return stack;
            }

            if (!canCombine(stack, stackInSlot)) {
                return stack;
            }

            if (!container.canPlaceItem(slot, stack)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), container.getMaxStackSize()) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    container.setItem(slot, copy);
                    container.setChanged();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    container.setItem(slot, copy);
                    container.setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!container.canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), container.getMaxStackSize());
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    container.setItem(slot, stack.split(m));
                    container.setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    container.setItem(slot, stack);
                    container.setChanged();
                }
                return ItemStack.EMPTY;
            }
        }
    }

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param container The container we are extracting from.
     * @param slot      Slot to extract from.
     * @param amount    Amount to extract (may be greater than the current stack's max limit)
     * @param simulate  If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @NotNull
    public static ItemStack extractItem(Container container, int slot, int amount, boolean simulate) {
        if (amount == 0){
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = container.getItem(slot);
        if (stackInSlot.isEmpty()){
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = container.removeItem(slot, m);
            container.setChanged();
            return decrStackSize;
        }
    }
}
