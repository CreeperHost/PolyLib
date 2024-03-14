package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class NeoFluidManager implements FluidManager {

    @Override
    public PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side) {
        LazyOptional<IFluidHandler> opt = block.getCapability(Capabilities.FLUID_HANDLER, side);
        if (opt.isPresent()) {
            return new NeoPolyFluidWrapper(opt.orElseThrow(IllegalStateException::new));
        }
        return null;
    }

    @Override
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        LazyOptional<IFluidHandlerItem> optional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
        if (optional.isPresent()) {
            return new NeoPolyFluidItemWrapper(optional.orElseThrow(IllegalStateException::new));
        }
        return null;
    }
}
