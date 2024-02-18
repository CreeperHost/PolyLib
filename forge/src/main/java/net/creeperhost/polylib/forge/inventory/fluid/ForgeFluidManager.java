package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class ForgeFluidManager implements FluidManager {

    @Override
    public PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side) {
        LazyOptional<IFluidHandler> opt = block.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
        if (opt.isPresent()) {
            return new ForgePolyFluidWrapper(opt.orElseThrow(IllegalStateException::new));
        }
        return null;
    }

    @Override
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        LazyOptional<IFluidHandlerItem> optional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (optional.isPresent()) {
            return new ForgePolyFluidItemWrapper(optional.orElseThrow(IllegalStateException::new));
        }
        return null;
    }
}
