package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityInventoryTest extends PolyBlockEntity
{
    public BlockEntityInventoryTest(BlockPos pos, BlockState state)
    {
        super(TestBlocks.TEST_BLOCK_ENTITY.get(), pos, state);
    }
}
