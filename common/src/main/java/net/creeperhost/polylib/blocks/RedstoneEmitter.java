package net.creeperhost.polylib.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Can be implemented by any {@link BlockEntity} whose block extends {@link PolyEntityBlock}.
 * Note: canProvidePower must be set to true on the {@link PolyEntityBlock} constructor.
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public interface RedstoneEmitter {

    int getWeakPower(BlockState blockState, Direction side);

    int getStrongPower(BlockState blockState, Direction side);
}
