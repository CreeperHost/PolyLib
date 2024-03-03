package net.creeperhost.polylib.forge.inventory.power;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class NeoEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        LazyOptional<IEnergyStorage> opt = block.getCapability(Capabilities.ENERGY, side);
        if (opt.isPresent()) {
            return new NeoPolyEnergyWrapper(opt.orElseThrow(IllegalStateException::new));
        }
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorage getItemEnergyStorage(ItemStack stack) {
        LazyOptional<IEnergyStorage> optional = stack.getCapability(Capabilities.ENERGY);
        if (optional.isPresent()) {
            return new NeoPolyEnergyWrapper(optional.orElseThrow(IllegalStateException::new));
        }
        return null;
    }
}
