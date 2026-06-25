package net.creeperhost.polylib.containers.slots;

import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Slot that only accepts items contained in a configured item tag.
 */
public class SlotTag extends Slot
{
    private final TagKey<Item> tagKey;

    /**
     * Creates a tag-filtered slot.
     *
     * @param container the container that owns this slot
     * @param i         the slot index inside the container
     * @param j         the x position used by vanilla slot rendering
     * @param k         the y position used by vanilla slot rendering
     * @param tagKey    the item tag accepted by this slot
     */
    public SlotTag(Container container, int i, int j, int k, @NotNull TagKey<Item> tagKey)
    {
        super(container, i, j, k);
        this.tagKey = tagKey;
    }

    /**
     * Allows placement only when the stack belongs to this slot's tag.
     */
    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack)
    {
        return itemStack.is(getTagKey());
    }

    /**
     * @return the item tag accepted by this slot
     */
    public TagKey<Item> getTagKey()
    {
        return tagKey;
    }
}
