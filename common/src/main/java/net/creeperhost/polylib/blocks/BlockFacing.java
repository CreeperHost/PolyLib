package net.creeperhost.polylib.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class BlockFacing extends BaseEntityBlock
{
    /**
     * A very simple implementation of a @link {@link BaseEntityBlock} that automatically handles rotations
     */
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockFacing(Properties properties)
    {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * Override the default {@link BlockState} when placed by a player setting a {@link Direction} for the facing of the block
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * Set the {@link RenderShape} to allow the block to bind a model and not render as an invisible texture
     */
    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    /**
     * Add our new {@link BlockState} to the {@link StateDefinition.Builder}
     */
    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }
}
