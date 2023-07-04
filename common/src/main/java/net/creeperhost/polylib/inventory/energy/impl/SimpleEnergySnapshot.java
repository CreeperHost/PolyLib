package net.creeperhost.polylib.inventory.energy.impl;

import net.creeperhost.polylib.inventory.energy.EnergySnapshot;
import net.creeperhost.polylib.inventory.energy.PolyEnergyContainer;

public class SimpleEnergySnapshot implements EnergySnapshot
{
    private final long energy;

    public SimpleEnergySnapshot(PolyEnergyContainer container)
    {
        this.energy = container.getStoredEnergy();
    }

    @Override
    public void loadSnapshot(PolyEnergyContainer container)
    {
        container.setEnergy(energy);
    }
}
