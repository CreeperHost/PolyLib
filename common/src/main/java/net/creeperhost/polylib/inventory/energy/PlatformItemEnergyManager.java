package net.creeperhost.polylib.inventory.energy;

import net.creeperhost.polylib.inventory.item.ItemStackHolder;

@Deprecated
public interface PlatformItemEnergyManager
{
    long getStoredEnergy();

    long getCapacity();

    long extract(ItemStackHolder holder, long amount, boolean simulate);

    long insert(ItemStackHolder holder, long amount, boolean simulate);

    boolean supportsInsertion();

    boolean supportsExtraction();
}
