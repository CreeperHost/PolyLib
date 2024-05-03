package net.creeperhost.testmod.blocks.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockTestMultiblockBlock extends Block implements EntityBlock
{
    public BlockTestMultiblockBlock()
    {
        super(Properties.of());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        return new TestMultiBlockBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if(level.getBlockEntity(blockPos) == null) return InteractionResult.FAIL;
        TestMultiBlockBlockEntity testMultiBlockBlockEntity = (TestMultiBlockBlockEntity) level.getBlockEntity(blockPos);
        if(testMultiBlockBlockEntity.getMultiblockController() != null)
        {
            boolean assembled = testMultiBlockBlockEntity.getMultiblockController().isAssembled();
//            if(!level.isClientSide && !assembled)
//            {
//                testMultiBlockBlockEntity.getMultiblockController().checkIfMachineIsWhole();
//                var s = testMultiBlockBlockEntity.getMultiblockController().getLastValidationException().getMessage();
//                player.displayClientMessage(Component.literal(s), false);
//                return InteractionResult.FAIL;
//            }
            if(level.isClientSide)
            {
                player.displayClientMessage(Component.literal( "Client: Assembled " + assembled), false);
                return InteractionResult.SUCCESS;
            }
            else
            {
                player.sendSystemMessage(Component.literal("Server: Assembled " + assembled));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState blockState)
    {
        return RenderShape.MODEL;
    }
}
