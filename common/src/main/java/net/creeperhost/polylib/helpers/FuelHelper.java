package net.creeperhost.polylib.helpers;

import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class FuelHelper
{
    public static boolean isItemFuel(@Nonnull ItemStack itemStack)
    {
        return getItemBurnTime(itemStack) != 0;
    }

    public static int getItemBurnTime(@Nonnull ItemStack itemstack)
    {
        return FuelRegistry.get(itemstack);
    }

    public static void registerFuel(@Nonnull Item item, int value)
    {
        FuelRegistry.register(value, item);
    }
}
