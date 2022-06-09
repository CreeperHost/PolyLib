package net.creeperhost.polylib.helpers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlayerHelper
{
    public static boolean doesPlayerHaveItemEquipped(Player player, Item item)
    {
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = player.getInventory().getArmor(i);
            if(stack.getItem() == item)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerWearingEquipment(Player player, EquipmentSlot equipmentSlot)
    {
        return !player.getInventory().getArmor(equipmentSlot.getIndex()).isEmpty();
    }
}
