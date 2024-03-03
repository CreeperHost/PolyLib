package net.creeperhost.polylib.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Can be implemented by any {@link BlockEntity} whose block extends {@link PolyEntityBlock}.
 * Allows the block entity to receive neighbor change events from the block.
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public interface ChangeListener {

    void onNeighborChange(Block fromBlock, BlockPos fromPos, boolean isMoving);
}
