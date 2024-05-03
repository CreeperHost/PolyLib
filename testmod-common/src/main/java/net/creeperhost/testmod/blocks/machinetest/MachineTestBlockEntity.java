package net.creeperhost.testmod.blocks.machinetest;

import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineTestBlockEntity extends BlockEntity implements PolyInventoryBlock, MenuProvider
{
    private final BlockInventory inventory = new BlockInventory(this, 1);

    public MachineTestBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.MACHINE_TEST_TILE.get(), blockPos, blockState);
    }

    @Override
    public Container getContainer(@Nullable Direction side) {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player)
    {
        return null;
    }
}
