package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class PolyNeoEnergyWrapper implements EnergyHandler {

    private final IPolyEnergyStorage storage;

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
            //TODO what about reversion? Does the neo impl have a snapshot system that needs to be implemented here?
            return (int) storage.receiveEnergy(maxAmount, false);
        }
        return 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        long extractedAmount = storage.extractEnergy(maxAmount, true);
        if (extractedAmount > 0) {
            return (int) storage.extractEnergy(maxAmount, false);
        }
        return 0;
    }
}
