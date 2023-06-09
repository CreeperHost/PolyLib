package net.creeperhost.polylib.helpers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlayerHelper
{
    public static boolean doesPlayerHaveItemEquipped(Player player, Item item)
    {
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = player.getInventory().getArmor(i);
            if (stack.getItem() == item)
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

    public static void giveItemStackToPlayer(Player player, ItemStack itemStack)
    {
        if (player == null) return;
        if (itemStack.isEmpty()) return;
        if (player.level() == null) return;

        //Try and add the itemStack to the player
        boolean added = player.addItem(itemStack);
        //If it fails due to the players inventory being full add an itementity of the stack below the player
        if (!added)
        {
            Level level = player.level();
            ItemEntity itemEntity = new ItemEntity(level, player.blockPosition().getX(), player.blockPosition().getY(),
                    player.blockPosition().getZ(), itemStack);
            level.addFreshEntity(itemEntity);
        }
    }
}
