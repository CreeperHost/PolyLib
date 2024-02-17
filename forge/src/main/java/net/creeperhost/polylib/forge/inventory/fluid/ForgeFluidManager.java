package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PlatformFluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class ForgeFluidManager implements PlatformFluidManager {

    @Override
    public PolyFluidHandler getFluidHandler(BlockEntity block, @Nullable Direction side) {
        LazyOptional<IFluidHandler> opt = block.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
        if (opt.isPresent()) {
            return new ForgePolyFluidWrapper(opt.orElseThrow(IllegalStateException::new));
        }
        return null;
    }
}
