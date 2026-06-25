package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.fabric.inventory.InitialContentsContainerItemContext;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Fabric implementation of {@link FluidManager}.
 * <p>
 * Wraps Fabric Transfer {@link Storage} instances in PolyLib's common fluid
 * storage interfaces.
 */
public class FabricFluidManager implements FluidManager {
    /**
     * Looks up Fabric's sided fluid storage API for a block entity.
     */
    @Override
    public @Nullable IPolyFluidStorage getBlockFluidStorage(BlockEntity block, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        return storage == null ? null : new FabricPolyFluidWrapper(storage);
    }

    /**
     * Looks up Fabric's item fluid storage API using a synthetic item context.
     */
    @Override
    public @Nullable IPolyFluidStorageItem getItemFluidStorage(ItemStack stack) {
        ContainerItemContext context = new InitialContentsContainerItemContext(stack);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, context);
        return storage == null ? null : new FabricPolyFluidItemWrapper(storage, context);
    }
}
