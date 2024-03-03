package net.creeperhost.polylib.inventory.energy;

import net.creeperhost.polylib.Serializable;
import net.minecraft.core.Direction;
import net.minecraft.world.Clearable;

@Deprecated
public interface PolyEnergyContainer extends Serializable, Clearable
{
    default PolyEnergyContainer getContainer(Direction direction)
    {
        return this;
    }

    long insertEnergy(long maxAmount, boolean simulate);

    default long internalInsert(long amount, boolean simulate)
    {
        return insertEnergy(amount, simulate);
    }

    long extractEnergy(long maxAmount, boolean simulate);

    default long internalExtract(long amount, boolean simulate)
    {
        return extractEnergy(amount, simulate);
    }

    void setEnergy(long energy);

    long getStoredEnergy();

    long getMaxCapacity();

    long maxInsert();

    long maxExtract();

    boolean allowsInsertion();

    boolean allowsExtraction();

    EnergySnapshot createSnapshot();

    default void readSnapshot(EnergySnapshot snapshot)
    {
        snapshot.loadSnapshot(this);
    }
}
