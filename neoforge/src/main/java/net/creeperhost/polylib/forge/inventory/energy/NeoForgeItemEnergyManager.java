package net.creeperhost.polylib.forge.inventory.energy;

import net.creeperhost.polylib.inventory.energy.PlatformItemEnergyManager;
import net.creeperhost.polylib.inventory.item.ItemStackHolder;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.CapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record NeoForgeItemEnergyManager(IEnergyStorage energy) implements PlatformItemEnergyManager
{
    public NeoForgeItemEnergyManager(CapabilityProvider<?> energyItem)
    {
        this(energyItem.getCapability(Capabilities.ENERGY).orElseThrow(IllegalArgumentException::new));
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
    public long extract(ItemStackHolder holder, long amount, boolean simulate)
    {
        return energy.extractEnergy((int) amount, simulate);
    }

    @Override
    public long insert(ItemStackHolder holder, long amount, boolean simulate)
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
