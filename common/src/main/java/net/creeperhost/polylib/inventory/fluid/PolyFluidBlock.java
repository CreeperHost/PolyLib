package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by brandon3055 on 16/02/2024
 */
public interface PolyFluidBlock {

    @Nullable
    PolyFluidHandler getFluidHandler(@Nullable Direction side);

}
