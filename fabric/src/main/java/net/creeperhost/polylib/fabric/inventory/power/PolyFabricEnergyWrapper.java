package net.creeperhost.polylib.fabric.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class PolyFabricEnergyWrapper extends SnapshotParticipant<Long> implements EnergyStorage {
    private final IPolyEnergyStorage storage;

    public PolyFabricEnergyWrapper(IPolyEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        long insertedAmount = storage.receiveEnergy(maxAmount, true);
        if (insertedAmount > 0) {
            updateSnapshots(transaction);
            return storage.receiveEnergy(maxAmount, false);
        }
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long extractedAmount = storage.extractEnergy(maxAmount, true);
        if (extractedAmount > 0) {
            updateSnapshots(transaction);
            return storage.extractEnergy(maxAmount, false);
        }
        return 0;
    }

    @Override
    public boolean supportsInsertion() {
        return storage.canReceive();
    }

    @Override
    public boolean supportsExtraction() {
        return storage.canExtract();
    }

    @Override
    public long getAmount() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
    }

    @Override
    protected Long createSnapshot() {
        return storage.getEnergyStored();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        storage.modifyEnergyStored(snapshot - storage.getEnergyStored());
    }
}
