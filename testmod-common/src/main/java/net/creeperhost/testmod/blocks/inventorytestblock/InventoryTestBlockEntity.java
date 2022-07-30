package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blockentity.BlockEntityInventory;
import net.creeperhost.polylib.containers.slots.SlotInput;
import net.creeperhost.polylib.containers.slots.SlotOutput;
import net.creeperhost.polylib.inventory.PolyInventory;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InventoryTestBlockEntity extends BlockEntityInventory
{
    int progress = 0;

    public InventoryTestBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.INVENTORY_TEST_TILE.get(), blockPos, blockState);
        setInventory(new PolyInventory(2));
        getInventoryOptional().ifPresent(polyInventory ->
        {
            addSlot(new SlotInput(polyInventory, 0, 41, 61));
            addSlot(new SlotOutput(polyInventory, 1, 121, 61));
        });
//        setContainerDataSize(1);
    }

    public void tick()
    {
        if(level != null && !level.isClientSide)
        {
            progress++;
            if(progress >= 100)
            {
                progress = 0;
            }
        }

        setContainerDataValue(0, () -> progress);
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.literal("Inventory Test");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory)
    {
        return new ContainerInventoryTestBlock(i, inventory, this, getContainerData());
    }
}
