package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.inventory.energy.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.energy.impl.SimpleEnergyContainer;
import net.creeperhost.polylib.inventory.energy.impl.WrappedBlockEnergyContainer;
import net.creeperhost.polylib.inventory.item.ItemInventoryBlock;
import net.creeperhost.polylib.inventory.item.SerializableContainer;
import net.creeperhost.polylib.inventory.item.SimpleItemInventory;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlockEntity extends BlockEntity implements PolyEnergyBlock<WrappedBlockEnergyContainer>, ItemInventoryBlock, MenuProvider
{
    int progress = 0;

    private SimpleItemInventory simpleItemInventory;
    private SimpleItemInventory outputInv = new SimpleItemInventory(this, 1);

    private WrappedBlockEnergyContainer energyContainer;

    public InventoryTestBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.INVENTORY_TEST_TILE.get(), blockPos, blockState);
    }

    public void tick()
    {
        if(level != null && !level.isClientSide)
        {
            progress++;
            if(progress >= 100)
            {
                progress = 0;
                getOutputContainer().setItem(0, new ItemStack(Items.DIAMOND));
            }
        }
    }

    @Override
    public SerializableContainer getContainer()
    {
        return simpleItemInventory == null ? this.simpleItemInventory = new SimpleItemInventory(this, 1) : this.simpleItemInventory;
    }

    public SerializableContainer getOutputContainer()
    {
        return outputInv == null ? this.outputInv = new SimpleItemInventory(this, 1) : this.outputInv;
    }

    @Override
    public WrappedBlockEnergyContainer getEnergyStorage()
    {
        return energyContainer == null ? this.energyContainer = new WrappedBlockEnergyContainer(this, new SimpleEnergyContainer(1000000)) : this.energyContainer;
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.literal("Inventory Test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player)
    {
        return new ContainerInventoryTestBlock(id, inventory, this);
    }
}
