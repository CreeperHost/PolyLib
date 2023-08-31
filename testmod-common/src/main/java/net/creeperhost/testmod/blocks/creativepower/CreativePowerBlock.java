package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativePowerBlock extends BlockFacing
{
    public CreativePowerBlock()
    {
        super(Properties.of());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new CreativePowerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType)
    {
        return (level1, blockPos, blockState1, blockEntity) ->
        {
            if(blockEntity instanceof CreativePowerBlockEntity creativePowerBlockEntity)
            {
                creativePowerBlockEntity.tick();
            }
        };
    }
}
