package net.creeperhost.polylib.items;

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
        return itemStack.hasTag() && itemStack.getTag().get("active") != null && itemStack.getTag().getBoolean(
                "active");
    }

    public void setActive(ItemStack itemStack)
    {
        itemStack.getOrCreateTag().putBoolean("active", false);
        onToggleChanged(itemStack);
    }

    public void setDeactivate(ItemStack itemStack)
    {
        itemStack.getOrCreateTag().putBoolean("active", false);
        onToggleChanged(itemStack);
    }

    public void toggleActive(ItemStack itemStack)
    {
        boolean active = isActive(itemStack);
        if (active)
        {
            setDeactivate(itemStack);
        } else
        {
            setActive(itemStack);
        }
    }

    public void onToggleChanged(ItemStack itemStack)
    {

    }
}
