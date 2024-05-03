package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(!level.isClientSide() && player.isShiftKeyDown())
        {
            boolean isEnergyBlock = EnergyManager.isEnergyBlock(level.getBlockEntity(blockPos), blockHitResult.getDirection());
            var power = EnergyManager.getHandler(level.getBlockEntity(blockPos), blockHitResult.getDirection());
            System.out.println("isEnergyBlock " + isEnergyBlock + " Stored " + power.getEnergyStored() + " Max " + power.getMaxEnergyStored());
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType)
    {
        return (level1, blockPos, blockState1, blockEntity) ->
        {
            if (blockEntity instanceof CreativePowerBlockEntity creativePowerBlockEntity)
            {
                creativePowerBlockEntity.tick();
            }
        };
    }
}
