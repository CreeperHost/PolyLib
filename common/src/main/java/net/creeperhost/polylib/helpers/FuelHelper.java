package net.creeperhost.polylib.helpers;

import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FuelHelper
{
    public static boolean isItemFuel(ItemStack itemStack)
    {
        return getItemBurnTime(itemStack) != 0;
    }

    public static int getItemBurnTime(ItemStack itemstack)
    {
        return FuelRegistry.get(itemstack);
    }

    public static void registerFuel(Item item, int value)
    {
        FuelRegistry.register(value, item);
    }
}
