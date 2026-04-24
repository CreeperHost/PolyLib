package net.creeperhost.testmod.blocks.creativepower;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PowerScreen extends AbstractContainerScreen<PowerContainer>
{
    public PowerScreen(PowerContainer abstractContainerMenu, Inventory inventory, Component component)
    {
        super(abstractContainerMenu, inventory, component);
    }
}
