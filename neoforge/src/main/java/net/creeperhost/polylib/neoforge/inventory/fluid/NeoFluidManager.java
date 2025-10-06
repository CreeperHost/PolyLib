package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
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
    @Deprecated
    public @Nullable PolyFluidHandlerItem getItemFluidHandler(ItemStack stack) {
        ResourceHandler<ItemResource> container = VanillaContainerWrapper.of(new SimpleContainer(stack) {
            @Override
            public void setItem(int slot, @NotNull ItemStack stack, boolean performSideEffects) {
                getItems().set(slot, stack);
            }
        });
        ItemAccess itemAccess = ItemAccess.forHandlerIndex(container, 0);
        ResourceHandler<FluidResource> handler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        if (handler != null) {
            return new NeoPolyFluidItemWrapper(handler, itemAccess);
        }
        return null;
    }
}
