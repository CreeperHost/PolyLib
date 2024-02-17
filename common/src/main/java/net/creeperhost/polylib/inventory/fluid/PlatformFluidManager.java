package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public interface PlatformFluidManager {

    /**
     * Returns a fluid handler instance for the specified BlockEntity if that entity supports forge/fabric fluid handling.
     * The returned handler is a wrapper that wraps the platform specific fluid implementation.
     * This wrapper does not account for things like capability invalidation, so you should not hold into it.
     *
     * @return a new {@link PolyFluidHandler} instance for the specified block, or null if the block does not support fluid handling.
     */
    @Nullable
    PolyFluidHandler getFluidHandler(BlockEntity block, @Nullable Direction side);
}
