package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class PolyNeoEnergyWrapper implements EnergyHandler {

    private final IPolyEnergyStorage storage;
    protected final EnergyJournal energyJournal = new EnergyJournal();

    public PolyNeoEnergyWrapper(IPolyEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public long getAmountAsLong() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        long insertedAmount = storage.receiveEnergy(maxAmount, true);
        if (insertedAmount > 0) {
            energyJournal.updateSnapshots(transaction);
            return (int) storage.receiveEnergy(maxAmount, false);
        }
        return 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        long extractedAmount = storage.extractEnergy(maxAmount, true);
        if (extractedAmount > 0) {
            energyJournal.updateSnapshots(transaction);
            return (int) storage.extractEnergy(maxAmount, false);
        }
        return 0;
    }

    protected class EnergyJournal extends SnapshotJournal<Long> {
        @Override
        protected Long createSnapshot() {
            return getAmountAsLong();
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            storage.modifyEnergyStored(snapshot - storage.getEnergyStored());
        }
    }
}
