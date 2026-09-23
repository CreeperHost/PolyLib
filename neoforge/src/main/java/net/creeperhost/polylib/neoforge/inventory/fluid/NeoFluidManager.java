package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge implementation of {@link FluidManager}.
 * <p>
 * Wraps NeoForge transfer {@link ResourceHandler} fluid capabilities in
 * PolyLib's common fluid storage interfaces.
 */
public class NeoFluidManager implements FluidManager {
    /**
     * Looks up NeoForge's block fluid transfer capability.
     */
    @Override
    public @Nullable IPolyFluidStorage getBlockFluidStorage(BlockEntity block, @Nullable Direction side) {
        ResourceHandler<FluidResource> handler = block.getLevel().getCapability(Capabilities.Fluid.BLOCK, block.getBlockPos(), side);
        return handler == null ? null : new NeoPolyFluidWrapper(handler);
    }

    /**
     * Looks up NeoForge's item fluid transfer capability through an {@link ItemAccess}.
     */
    @Override
    public @Nullable IPolyFluidStorageItem getItemFluidStorage(ItemStack stack) {
        ResourceHandler<ItemResource> container = VanillaContainerWrapper.of(new SimpleContainer(stack) {
            @Override
            public void setItem(int slot, @NotNull ItemStack stack, boolean performSideEffects) {
                getItems().set(slot, stack);
            }
        });
        ItemAccess itemAccess = ItemAccess.forHandlerIndex(container, 0);
        ResourceHandler<FluidResource> handler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : new NeoPolyFluidItemWrapper(handler, itemAccess);
    }
}
