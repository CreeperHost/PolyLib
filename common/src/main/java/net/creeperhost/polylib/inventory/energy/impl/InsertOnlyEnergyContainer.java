package net.creeperhost.polylib.inventory.energy.impl;

import net.minecraft.util.Mth;

public class InsertOnlyEnergyContainer extends SimpleEnergyContainer
{
    public InsertOnlyEnergyContainer(long energyCapacity)
    {
        super(energyCapacity);
    }

    @Override
    public long maxExtract()
    {
        return 0;
    }

    @Override
    public boolean allowsExtraction()
    {
        return false;
    }

    @Override
    public long extractEnergy(long maxAmount, boolean simulate)
    {
        return 0;
    }

    @Override
    public long internalExtract(long maxAmount, boolean simulate)
    {
        long extracted = (long) Mth.clamp(maxAmount, 0, getStoredEnergy());
        if (simulate) return extracted;
        this.setEnergy(getStoredEnergy() - extracted);
        return extracted;
    }
}
