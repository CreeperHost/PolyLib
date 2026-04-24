package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeoEnergyManager implements EnergyManager {
    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        EnergyHandler handler = Capabilities.Energy.BLOCK.getCapability(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (handler != null) {
            return new NeoPolyEnergyWrapper(handler);
        }
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        ResourceHandler<ItemResource> container = VanillaContainerWrapper.of(new SimpleContainer(stack) {
            @Override
            public void setItem(int slot, @NotNull ItemStack stack, boolean performSideEffects) {
                getItems().set(slot, stack);
            }
        });
        ItemAccess itemAccess = ItemAccess.forHandlerIndex(container, 0);
        EnergyHandler handler = itemAccess.getCapability(Capabilities.Energy.ITEM);
        if (handler != null) {
            return new NeoPolyEnergyItemWrapper(handler, itemAccess);
        }
        return null;
    }
}
