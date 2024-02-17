package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PlatformFluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class FabricFluidManager implements PlatformFluidManager {

    @Override
    public @Nullable PolyFluidHandler getFluidHandler(BlockEntity block, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (storage == null) return null;
        return new FabricPolyFluidWrapper(storage);
    }
}
