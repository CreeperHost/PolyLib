package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class InventoryTestBlockEntity extends BaseContainerBlockEntity
{
    public InventoryTestBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.INVENTORY_TEST_TILE.get(), blockPos, blockState);
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.literal("test");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory)
    {
        return new ContainerInventoryTestBlock(i, inventory, this, new SimpleContainerData(0));
    }

    @Override
    public int getContainerSize()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack getItem(int i)
    {
        return null;
    }

    @Override
    public ItemStack removeItem(int i, int j)
    {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i)
    {
        return null;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack)
    {

    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {

    }
}
