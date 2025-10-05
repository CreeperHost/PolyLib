package net.creeperhost.polylib.neoforge.inventory.fluid;

import com.mojang.blaze3d.resource.ResourceHandle;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class NeoFluidManager implements FluidManager {

    @Override
    public PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side) {
        ResourceHandler<FluidResource> handler = Capabilities.Fluid.BLOCK.getCapability(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (handler != null) {
            return new NeoPolyFluidWrapper(handler);
        }
        return null;
    }

    @Override
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        ResourceHandler<FluidResource> handler = Capabilities.Fluid.ITEM.getCapability(stack, null);
        if (handler != null) {
            return new NeoPolyFluidItemWrapper(handler);
        }
        return null;
    }
}
