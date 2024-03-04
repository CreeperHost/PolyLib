package net.creeperhost.polylib.inventory.energy;

@Deprecated
public interface PlatformEnergyManager
{
    long getStoredEnergy();

    long getCapacity();

    long extract(long amount, boolean simulate);

    long insert(long amount, boolean simulate);

    boolean supportsInsertion();

    boolean supportsExtraction();
}
