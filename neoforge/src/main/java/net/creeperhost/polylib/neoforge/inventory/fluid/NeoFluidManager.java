package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class NeoFluidManager implements FluidManager {

    @Override
    public PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side) {
        IFluidHandler handler = Capabilities.FluidHandler.BLOCK.getCapability(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (handler != null) {
            return new NeoPolyFluidWrapper(handler);
        }
        return null;
    }

    @Override
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        IFluidHandlerItem handler = Capabilities.FluidHandler.ITEM.getCapability(stack, null);
        if (handler != null) {
            return new NeoPolyFluidItemWrapper(handler);
        }
        return null;
    }
}
