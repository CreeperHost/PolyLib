package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class NeoEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        IEnergyStorage storage = Capabilities.EnergyStorage.BLOCK.getCapability(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (storage != null) {
            return new NeoPolyEnergyWrapper(storage);
        }
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        IEnergyStorage storage = Capabilities.EnergyStorage.ITEM.getCapability(stack, null);
        if (storage != null) {
            return new NeoPolyEnergyWrapper(storage, stack);
        }
        return null;
    }
}
