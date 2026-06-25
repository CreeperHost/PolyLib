package net.creeperhost.polylib.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

/**
 * Basic input slot for menu layouts that want an explicit input-slot type.
 * <p>
 * This currently behaves the same as a vanilla {@link Slot}; use {@link PolySlot}
 * when custom validation, stack limits, or active-state behavior is needed.
 */
public class SlotInput extends Slot
{
    /**
     * Creates an input slot.
     *
     * @param container the container that owns this slot
     * @param i         the slot index inside the container
     * @param j         the x position used by vanilla slot rendering
     * @param k         the y position used by vanilla slot rendering
     */
    public SlotInput(Container container, int i, int j, int k)
    {
        super(container, i, j, k);
    }
}
