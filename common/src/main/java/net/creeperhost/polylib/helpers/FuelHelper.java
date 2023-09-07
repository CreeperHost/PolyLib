package net.creeperhost.polylib.helpers;

import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FuelHelper
{
    public static boolean isItemFuel(@NotNull ItemStack itemStack)
    {
        return getItemBurnTime(itemStack) != 0;
    }

    public static int getItemBurnTime(@NotNull ItemStack itemstack)
    {
        return FuelRegistry.get(itemstack);
    }

    public static void registerFuel(@NotNull Item item, int value)
    {
        FuelRegistry.register(value, item);
    }
}
