package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PowerContainer extends AbstractContainerMenu
{
    public PowerContainer(int i, Inventory playerInventory, FriendlyByteBuf buf)
    {
        super(TestContainers.POWER_CONTAINEr.get(), i);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
