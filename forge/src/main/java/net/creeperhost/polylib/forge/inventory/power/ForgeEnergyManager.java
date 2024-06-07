package net.creeperhost.polylib.forge.inventory.power;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class ForgeEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        LazyOptional<IEnergyStorage> opt = block.getCapability(ForgeCapabilities.ENERGY, side);
        if (opt.isPresent()) {
            return new ForgePolyEnergyWrapper(opt.orElseThrow(IllegalStateException::new));
        }
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        LazyOptional<IEnergyStorage> optional = stack.getCapability(ForgeCapabilities.ENERGY);
        if (optional.isPresent()) {
            return new ForgePolyEnergyWrapper(optional.orElseThrow(IllegalStateException::new), stack);
        }
        return null;
    }
}
