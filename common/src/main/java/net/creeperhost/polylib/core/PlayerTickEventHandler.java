package net.creeperhost.polylib.core;

import dev.architectury.event.events.common.TickEvent;
import net.creeperhost.polylib.events.ArmourChangedEvent;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class PlayerTickEventHandler
{
    //TODO
//    private static final HashMap<Player, NonNullList<ItemStack>> MAP = new HashMap<>();

    private static final ItemStack[] current = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };

    public static void init()
    {
        TickEvent.PLAYER_POST.register(player ->
        {
            if(player != null)
            {
                for (int i = 0; i < 4; i++)
                {
                    ItemStack stack = player.getInventory().getArmor(i);
                    if(!stack.isEmpty())
                    {
                        if(!stack.sameItem(current[i]))
                        {
                            ArmourChangedEvent.ARMOUR_EQUIPPED_EVENT.invoker().onArmourEquipped(player, current[i], stack.copy());
                        }
                        current[i] = stack.copy();
                    }
                    if(stack.isEmpty() && !current[i].isEmpty())
                    {
                        //Armour has changed
                        ArmourChangedEvent.ARMOUR_UNEQUIPPED_EVENT.invoker().onArmourUnEquipped(player, current[i], stack.copy());
                        current[i] = stack.copy();
                    }
                }
            }
        });
    }
}
