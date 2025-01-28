package net.creeperhost.polylib.helpers;

import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FuelHelper
{
    public static boolean isItemFuel(@NotNull ItemStack itemStack, Level level)
    {
        return getItemBurnTime(itemStack, level) != 0;
    }

    public static int getItemBurnTime(@NotNull ItemStack itemstack, Level level)
    {
        return FuelRegistry.get(itemstack, null, level.fuelValues());
    }

    public static void registerFuel(@NotNull Item item, int value)
    {
        FuelRegistry.register(value, item);
    }
}
