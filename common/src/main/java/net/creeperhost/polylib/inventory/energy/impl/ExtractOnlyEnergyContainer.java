package net.creeperhost.polylib.inventory.energy.impl;

import net.minecraft.util.Mth;

public class ExtractOnlyEnergyContainer extends SimpleEnergyContainer
{
    public ExtractOnlyEnergyContainer(long energyCapacity)
    {
        super(energyCapacity);
    }

    @Override
    public long maxInsert()
    {
        return 0;
    }

    @Override
    public boolean allowsInsertion()
    {
        return false;
    }

    @Override
    public long insertEnergy(long maxAmount, boolean simulate)
    {
        return 0;
    }

    @Override
    public long internalInsert(long maxAmount, boolean simulate)
    {
        long inserted = (long) Mth.clamp(maxAmount, 0, getMaxCapacity() - getStoredEnergy());
        if (simulate) return inserted;
        this.setEnergy(getStoredEnergy() + inserted);
        return inserted;
    }
}
