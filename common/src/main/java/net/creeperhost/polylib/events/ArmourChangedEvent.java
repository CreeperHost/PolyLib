package net.creeperhost.polylib.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Deprecated(forRemoval = true)
public interface ArmourChangedEvent
{
    Event<ArmourChangedEvent.ArmourEquippedEvent> ARMOUR_EQUIPPED_EVENT = EventFactory.createEventResult();
    Event<ArmourChangedEvent.ArmourUnEquippedEvent> ARMOUR_UNEQUIPPED_EVENT = EventFactory.createEventResult();

    interface ArmourEquippedEvent
    {
        void onArmourEquipped(Player player, ItemStack oldStack, ItemStack newStack);
    }

    interface ArmourUnEquippedEvent
    {
        void onArmourUnEquipped(Player player, ItemStack oldStack, ItemStack newStack);
    }
}
