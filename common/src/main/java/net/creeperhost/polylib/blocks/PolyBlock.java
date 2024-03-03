package net.creeperhost.polylib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base block class for PolyLib,
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public class PolyBlock extends Block {

    protected boolean canProvidePower = false;
    private boolean isLightTransparent = false;

    public PolyBlock(Properties properties) {
        super(properties);
    }

    public PolyBlock setLightTransparent() {
        isLightTransparent = true;
        return this;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return isLightTransparent ? 1F : super.getShadeBrightness(state, getter, pos);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return canProvidePower;
    }

    public static int getRedstonePower(LevelReader world, BlockPos pos, Direction facing) {
        BlockState state = world.getBlockState(pos);
        return state.isRedstoneConductor(world, pos) ? getStrongPower(world, pos) : state.getSignal(world, pos, facing);
    }

    public static int getStrongPower(LevelReader level, BlockPos pos) {
        int power = 0;
        for (Direction dir : Direction.values()) {
            power = Math.max(power, level.getDirectSignal(pos.relative(dir), Direction.DOWN));
            if (power >= 15) break;
        }
        return power;
    }

    public static boolean isBlockPowered(LevelReader world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (getRedstonePower(world, pos.relative(dir), dir) > 0) return true;
        }
        return false;
    }
}

