package net.creeperhost.polylib.fabric.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class FabricPolyEnergyWrapper implements IPolyEnergyStorage {
    private final EnergyStorage storage;

    public FabricPolyEnergyWrapper(EnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()){
            long inserted = storage.insert(maxReceive, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return inserted;
        }
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()){
            long extracted = storage.extract(maxExtract, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted;
        }
    }

    @Override
    public long getEnergyStored() {
        return storage.getAmount();
    }

    @Override
    public long getMaxEnergyStored() {
        return storage.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return storage.supportsExtraction();
    }

    @Override
    public boolean canReceive() {
        return storage.supportsInsertion();
    }

    @Override
    public long modifyEnergyStored(long amount) {
        amount = Math.min(Math.max(amount, Integer.MIN_VALUE), Integer.MAX_VALUE);
        if (amount > 0) {
            return receiveEnergy((int) amount, false);
        } else {
            return extractEnergy((int) -amount, false);
        }
    }
}
