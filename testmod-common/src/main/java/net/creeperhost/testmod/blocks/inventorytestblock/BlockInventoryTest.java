package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockInventoryTest extends BlockFacing
{
    public BlockInventoryTest(Properties properties)
    {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new BlockEntityInventoryTest(blockPos, blockState);
    }
}
