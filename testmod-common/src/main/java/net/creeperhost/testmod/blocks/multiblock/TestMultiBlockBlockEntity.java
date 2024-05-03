package net.creeperhost.testmod.blocks.multiblock;

import net.creeperhost.polylib.helpers.LevelHelper;
import net.creeperhost.polylib.mulitblock.MultiblockControllerBase;
import net.creeperhost.polylib.mulitblock.MultiblockValidationException;
import net.creeperhost.polylib.mulitblock.rectangular.RectangularMultiblockTileEntityBase;
import net.creeperhost.testmod.MultiBlockTest;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestMultiBlockBlockEntity extends RectangularMultiblockTileEntityBase
{
    public TestMultiBlockBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.MULTIBLOCK_TEST_TILE.get(), blockPos, blockState);
    }

    @Override
    public Class<? extends MultiblockControllerBase> getMultiblockControllerType()
    {
        return MultiBlockTest.class;
    }

    @Override
    public void onMachineActivated()
    {

    }

    @Override
    public void onMachineDeactivated()
    {

    }

    @Override
    public MultiblockControllerBase createNewMultiblock()
    {
        return new MultiBlockTest(getLevel());
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException
    {
        if (level == null) return;
        if (getBlockPos() == null) return;
        if (level.getBlockState(getBlockPos()) == null) return;
        Block block = level.getBlockState(getBlockPos()).getBlock();
        if (block != TestBlocks.MULTIBLOCK_TEST_BLOCK.get())
        {
            throw new MultiblockValidationException(block.getDescriptionId() + " is not valid for the frame of the block" + getBlockPos());
        }
    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException
    {
        if (level == null) return;
        if (getBlockPos() == null) return;
        if (level.getBlockState(getBlockPos()) == null) return;
        Block block = level.getBlockState(getBlockPos()).getBlock();
        if (block != TestBlocks.MULTIBLOCK_TEST_BLOCK.get())
        {
            throw new MultiblockValidationException(block.getDescriptionId() + " is not valid for the frame of the block" + getBlockPos());
        }
    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException
    {
        if (level == null) return;
        if (getBlockPos() == null) return;
        if (level.getBlockState(getBlockPos()) == null) return;
        Block block = level.getBlockState(getBlockPos()).getBlock();
        if (block != TestBlocks.MULTIBLOCK_TEST_BLOCK.get())
        {
            throw new MultiblockValidationException(block.getDescriptionId() + " is not valid for the frame of the block" + getBlockPos());
        }
    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException
    {
        if (level == null) return;
        if (getBlockPos() == null) return;
        if (level.getBlockState(getBlockPos()) == null) return;
        Block block = level.getBlockState(getBlockPos()).getBlock();
        if (block != TestBlocks.MULTIBLOCK_TEST_BLOCK.get())
        {
            throw new MultiblockValidationException(block.getDescriptionId() + " is not valid for the frame of the block" + getBlockPos());
        }
    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException
    {
        if (level == null) return;
        if (getBlockPos() == null) return;
        if (level.getBlockState(getBlockPos()) == null) return;
        Block block = level.getBlockState(getBlockPos()).getBlock();
        if (block != TestBlocks.MULTIBLOCK_TEST_BLOCK.get())
        {
            throw new MultiblockValidationException(block.getDescriptionId() + " is not valid for the frame of the block" + getBlockPos());
        }
    }

    @Override
    public Component getDisplayName()
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
