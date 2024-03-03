package net.creeperhost.polylib.forge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class PolyForgeEnergyWrapper implements IEnergyStorage {

    private final IPolyEnergyStorage storage;

    public PolyForgeEnergyWrapper(IPolyEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, storage.getEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, storage.getMaxEnergyStored());
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }
}
