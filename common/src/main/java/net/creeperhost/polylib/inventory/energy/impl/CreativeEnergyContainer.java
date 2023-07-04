package net.creeperhost.polylib.inventory.energy.impl;

public class CreativeEnergyContainer extends ExtractOnlyEnergyContainer
{
    public CreativeEnergyContainer(long maxCapacity)
    {
        super(maxCapacity);
    }

    @Override
    public long extractEnergy(long maxAmount, boolean simulate)
    {
        return maxAmount;
    }

    @Override
    public long getStoredEnergy()
    {
        return getMaxCapacity();
    }
}
