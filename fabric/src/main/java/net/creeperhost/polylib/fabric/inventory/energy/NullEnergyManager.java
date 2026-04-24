package net.creeperhost.polylib.fabric.inventory.energy;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 03/05/2025
 */
public class NullEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        return null;
    }
}
