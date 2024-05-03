package net.creeperhost.polylib.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static net.creeperhost.polylib.init.DataComps.ITEM_TOGGLE_ACTIVE;

public class ItemToggle extends Item
{

    public ItemToggle(Properties properties)
    {
        super(properties);
    }

    public boolean isActive(ItemStack itemStack)
    {
        return itemStack.getOrDefault(ITEM_TOGGLE_ACTIVE.get(), false);
    }

    public void setActive(ItemStack itemStack, boolean active)
    {
        if (isActive(itemStack) != active) {
            itemStack.set(ITEM_TOGGLE_ACTIVE.get(), active);
            onToggleChanged(itemStack);
        }
    }

    public void setActive(ItemStack itemStack)
    {
        itemStack.set(ITEM_TOGGLE_ACTIVE.get(), true);
        onToggleChanged(itemStack);
    }

    public void setDeactivate(ItemStack itemStack)
    {
        itemStack.set(ITEM_TOGGLE_ACTIVE.get(), false);
        onToggleChanged(itemStack);
    }

    public void toggleActive(ItemStack itemStack)
    {
        setActive(itemStack, !isActive(itemStack));
    }

    public void onToggleChanged(ItemStack itemStack)
    {

    }
}
