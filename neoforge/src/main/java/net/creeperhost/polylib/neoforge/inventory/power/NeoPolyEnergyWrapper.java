package net.creeperhost.polylib.neoforge.inventory.power;

import com.google.common.primitives.Ints;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class NeoPolyEnergyWrapper implements IPolyEnergyStorage {

    private final EnergyHandler handler;

    public NeoPolyEnergyWrapper(EnergyHandler handler) {
        this.handler = handler;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        try (Transaction transaction = Transaction.open(null)){
            long inserted = handler.insert(Ints.saturatedCast(maxReceive), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return inserted;
        }
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        try (Transaction transaction = Transaction.open(null)){
            long extracted = handler.extract(Ints.saturatedCast(maxExtract), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted;
        }
    }

    @Override
    public long getEnergyStored() {
        return handler.getAmountAsLong();
    }

    @Override
    public long getMaxEnergyStored() {
        return handler.getCapacityAsLong();
    }

    @Override
    public boolean canExtract() {
        return handler.getAmountAsLong() > 0;
    }

    @Override
    public boolean canReceive() {
        return handler.getAmountAsLong() < handler.getCapacityAsLong();
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
