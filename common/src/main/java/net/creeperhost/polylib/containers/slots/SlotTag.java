package net.creeperhost.polylib.containers.slots;

import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotTag extends Slot
{
    TagKey<Item> tagKey;

    public SlotTag(Container container, int i, int j, int k, @NotNull TagKey<Item> tagKey)
    {
        super(container, i, j, k);
        this.tagKey = tagKey;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack)
    {
        return itemStack.is(tagKey);
    }
}
