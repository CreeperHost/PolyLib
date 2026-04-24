package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class BlockInventoryTest extends BlockFacing
{
    public BlockInventoryTest(Properties properties)
    {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
//        if(!level.isClientSide() && player.isShiftKeyDown())
//        {
//            boolean isEnergyBlock = EnergyManager.isEnergyBlock(level.getBlockEntity(blockPos), blockHitResult.getDirection());
//            var power = EnergyManager.getHandler(level.getBlockEntity(blockPos), blockHitResult.getDirection());
//            System.out.println("isEnergyBlock " + isEnergyBlock + " Stored " + power.getEnergyStored() + " Max " + power.getMaxEnergyStored());
//            return InteractionResult.SUCCESS;
//        }
        if (!level.isClientSide())
        {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            Services.REGISTER_HELPER.openMenu((ServerPlayer) player, (MenuProvider) blockEntity, buf -> buf.writeBlockPos(pos));
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
            if(blockEntity instanceof BlockEntityInventoryTest inventoryTestBlock)
            {
                inventoryTestBlock.tick();
            }
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new BlockEntityInventoryTest(blockPos, blockState);
    }
}
