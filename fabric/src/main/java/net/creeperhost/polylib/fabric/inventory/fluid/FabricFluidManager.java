package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.fabric.inventory.InitialContentsContainerItemContext;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class FabricFluidManager implements FluidManager {

    @Override
    public @Nullable PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (storage == null) return null;
        return new FabricPolyFluidWrapper(storage);
    }

    @Override
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        ContainerItemContext context = new InitialContentsContainerItemContext(stack);
        Storage<FluidVariant> storage =  FluidStorage.ITEM.find(stack, context);
        if (storage == null) return null;
        return new FabricPolyFluidIemWrapper(context, storage);
    }
}
