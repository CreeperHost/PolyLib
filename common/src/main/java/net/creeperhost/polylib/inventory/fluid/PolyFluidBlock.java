package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Implement on block entities that expose PolyLib fluid storage.
 */
public interface PolyFluidBlock {
    /**
     * Gets the fluid storage for the requested side.
     *
     * @param side queried side, or null for unsided access
     * @return fluid storage, or null when unavailable
     */
    IPolyFluidStorage getFluidStorage(@Nullable Direction side);
}
