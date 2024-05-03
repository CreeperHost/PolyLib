package net.creeperhost.polylib.inventory.power;

import net.creeperhost.polylib.Serializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Simple implementation of IPolyEnergyStorage that ensures the given block entity is marked dirty when energy content changes.
 * <p>
 * Created by brandon3055 on 26/02/2024
 */
public class PolyEnergyStorage implements IPolyEnergyStorage, Serializable {
    protected Runnable changeListener;
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
    protected boolean allowExtract = true;
    protected boolean allowReceive = true;

    public PolyEnergyStorage(long capacity) {
        this(capacity, capacity);
    }

    public PolyEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public PolyEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, null);
    }

    public PolyEnergyStorage(long capacity, long maxReceive, long maxExtract, Runnable changeListener) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.changeListener = changeListener;
    }

    public PolyEnergyStorage setIOMode(boolean allowExtract, boolean allowReceive) {
        this.allowExtract = allowExtract;
        this.allowReceive = allowReceive;
        return this;
    }

    public PolyEnergyStorage setExtractOnly() {
        return setIOMode(true, false);
    }

    public PolyEnergyStorage setReceiveOnly() {
        return setIOMode(false, true);
    }

    /**
     * @param inputOutput true = Input Only, false = Output Only.
     */
    public PolyEnergyStorage setIOMode(boolean inputOutput) {
        return setIOMode(!inputOutput, inputOutput);
    }

    public PolyEnergyStorage setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    public PolyEnergyStorage setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public PolyEnergyStorage setMaxReceive(long maxReceive) {
        this.maxReceive = maxReceive;
        return this;
    }

    public PolyEnergyStorage setMaxTransfer(long maxTransfer) {
        this.maxReceive = this.maxExtract = maxTransfer;
        return this;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        long energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(this.maxReceive(), maxReceive));
        if (!simulate) {
            energy += energyReceived;
            if (energyReceived != 0) markDirty();
        }
        return energyReceived;
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract(), maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            if (energyExtracted != 0) markDirty();
        }
        return energyExtracted;
    }

    @Override
    public long getEnergyStored() {
        return energy;
    }

    @Override
    public long getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return allowExtract && maxExtract() > 0;
    }

    @Override
    public boolean canReceive() {
        return allowReceive && maxReceive() > 0;
    }

    public long maxExtract() {
        return maxExtract;
    }

    public long maxReceive() {
        return maxReceive;
    }

    @Override
    public long modifyEnergyStored(long amount) {
        if (amount > getMaxEnergyStored() - energy) {
            amount = getMaxEnergyStored() - energy;
        } else if (amount < -energy) {
            amount = -energy;
        }

        energy += amount;
        if (amount != 0) markDirty();
        return Math.abs(amount);
    }

    public void markDirty() {
        if (changeListener != null) changeListener.run();
    }

    @Override
    public CompoundTag serialize(HolderLookup.Provider provider, CompoundTag nbt) {
        nbt.putLong("energy", energy);
        return nbt;
    }

    @Override
    public void deserialize(HolderLookup.Provider provider, CompoundTag nbt) {
        energy = nbt.getLong("energy");
    }

    public void readFromBuf(FriendlyByteBuf buf) {
        buf.writeVarLong(energy);
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        energy = buf.readVarLong();
    }
}
