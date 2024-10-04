package net.creeperhost.polylib.items;

import net.creeperhost.polylib.init.DataComps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class ItemToggle extends Item
{

    public ItemToggle(Properties properties)
    {
        super(properties);
    }

    public boolean isActive(ItemStack itemStack)
    {
        return itemStack.getOrDefault(DataComps.getItemToggleActive(), false);
    }

    public void setActive(ItemStack itemStack, boolean active)
    {
        if (isActive(itemStack) != active) {
            itemStack.set(DataComps.getItemToggleActive(), active);
            onToggleChanged(itemStack);
        }
    }

    public void setActive(ItemStack itemStack)
    {
        itemStack.set(DataComps.getItemToggleActive(), true);
        onToggleChanged(itemStack);
    }

    public void setDeactivate(ItemStack itemStack)
    {
        itemStack.set(DataComps.getItemToggleActive(), false);
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
