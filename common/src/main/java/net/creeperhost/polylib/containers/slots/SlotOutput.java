package net.creeperhost.polylib.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Output-only slot.
 * <p>
 * Players can take items from this slot, but {@link #mayPlace(ItemStack)} always
 * returns false so items cannot be inserted directly.
 */
public class SlotOutput extends Slot
{
    /**
     * Creates an output-only slot.
     *
     * @param container the container that owns this slot
     * @param i         the slot index inside the container
     * @param j         the x position used by vanilla slot rendering
     * @param k         the y position used by vanilla slot rendering
     */
    public SlotOutput(Container container, int i, int j, int k)
    {
        super(container, i, j, k);
    }

    /**
     * Blocks direct insertion into this output slot.
     *
     * @return false for every stack
     */
    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack)
    {
        return false;
    }
}
