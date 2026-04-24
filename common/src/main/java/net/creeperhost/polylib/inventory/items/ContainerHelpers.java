package net.creeperhost.polylib.inventory.items;

import net.creeperhost.polylib.helpers.ContainerUtil;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 04/04/2024
 */
public interface ContainerHelpers {

    default Container container() {
        return (Container) this;
    }

    /**
     * Inserts a stack into this container by first trying to merge it with any existing stacks in the container,
     * Then any remaining items will be placed in the first available slot.
     *
     * @param stack    The stack to be inserted.
     * @param simulate Allows you to simulate the insertion without actually doing anything.
     * @return the number of items that could not be inserted.
     */
    default int insertStack(@NotNull ItemStack stack, boolean simulate) {
        return ContainerUtil.insertStack(stack, container(), simulate);
    }

    /**
     * Matches the functionality of IItemHandler#insertItem from forge.
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     **/
    @NotNull
    default ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return ContainerUtil.insertItem(container(), slot, stack, simulate);
    }

    /**
     * Matches the functionality of IItemHandler#extractItem from forge.
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @NotNull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ContainerUtil.extractItem(container(), slot, amount, simulate);
    }
}
