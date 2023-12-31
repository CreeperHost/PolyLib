package net.creeperhost.testmod.blocks.creativepower;

import com.mojang.serialization.MapCodec;
import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.polylib.inventory.energy.EnergyHooks;
import net.creeperhost.testmod.blocks.inventorytestblock.InventoryTestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(!level.isClientSide() && player.isShiftKeyDown())
        {
            boolean isEnergyBlock = EnergyHooks.isEnergyContainer(level.getBlockEntity(blockPos), blockHitResult.getDirection());
            var power = EnergyHooks.getBlockEnergyManager(level.getBlockEntity(blockPos), blockHitResult.getDirection());
            System.out.println("isEnergyBlock " + isEnergyBlock + " Stored " + power.getStoredEnergy() + " Max " + power.getCapacity());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
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
