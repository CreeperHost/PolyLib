package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.inventory.PolyInventory;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    PolyInventory polyInventory = new PolyInventory(2)
    {
        @Override
        public void setChanged()
        {
            super.setChanged();
            InventoryTestBlockEntity.this.setChanged();
        }
    };

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
        return polyInventory.getContainerSize();
    }

    @Override
    public boolean isEmpty()
    {
        return polyInventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int i)
    {
        return polyInventory.getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j)
    {
        return polyInventory.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i)
    {
        return polyInventory.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack)
    {
        polyInventory.setItem(i, itemStack);
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
        polyInventory.clearContent();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag)
    {
        super.saveAdditional(compoundTag);
        compoundTag.merge(polyInventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag)
    {
        super.load(compoundTag);
        polyInventory.deserializeNBT(compoundTag);
    }
}
