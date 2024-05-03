package net.creeperhost.testmod.blocks.inventorytestblock;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlock extends BlockFacing
{
    public InventoryTestBlock()
    {
        super(Properties.of());
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
        if (!level.isClientSide)
        {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            MenuRegistry.openExtendedMenu((ServerPlayer) player, (MenuProvider) blockEntity, packetBuffer -> packetBuffer.writeBlockPos(blockEntity.getBlockPos()));
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
            if(blockEntity instanceof InventoryTestBlockEntity inventoryTestBlock)
            {
                inventoryTestBlock.tick();
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        return new InventoryTestBlockEntity(blockPos, blockState);
    }
}
