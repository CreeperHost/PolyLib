package net.creeperhost.polylib.forge.inventory.energy;

import net.creeperhost.polylib.inventory.energy.PlatformEnergyManager;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public record ForgeEnergyManager(IEnergyStorage energy) implements PlatformEnergyManager
{
    public ForgeEnergyManager(CapabilityProvider<?> energyItem, Direction direction)
    {
        this(energyItem.getCapability(ForgeCapabilities.ENERGY, direction).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public long getStoredEnergy()
    {
        return energy.getEnergyStored();
    }

    @Override
    public long getCapacity()
    {
        return energy.getMaxEnergyStored();
    }

    @Override
    public long extract(long amount, boolean simulate)
    {
        return energy.extractEnergy((int) amount, simulate);
    }

    @Override
    public long insert(long amount, boolean simulate)
    {
        return energy.receiveEnergy((int) amount, simulate);
    }

    @Override
    public boolean supportsInsertion()
    {
        return energy.canReceive();
    }

    @Override
    public boolean supportsExtraction()
    {
        return energy.canExtract();
    }
}
