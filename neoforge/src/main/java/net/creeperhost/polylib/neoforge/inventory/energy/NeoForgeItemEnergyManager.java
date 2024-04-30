package net.creeperhost.polylib.neoforge.inventory.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record NeoForgeItemEnergyManager(IEnergyStorage energy) implements PlatformItemEnergyManager
{
    public NeoForgeItemEnergyManager(ItemStack energyItem)
    {
        this(energyItem.getCapability(Capabilities.EnergyStorage.ITEM));
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
