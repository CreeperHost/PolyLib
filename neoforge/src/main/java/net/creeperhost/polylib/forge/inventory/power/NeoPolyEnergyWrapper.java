package net.creeperhost.polylib.forge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class NeoPolyEnergyWrapper implements IPolyEnergyStorageItem {
    private final IEnergyStorage storage;
    private ItemStack container = ItemStack.EMPTY;

    public NeoPolyEnergyWrapper(IEnergyStorage storage) {
        this.storage = storage;
    }

    public NeoPolyEnergyWrapper(IEnergyStorage storage, ItemStack container) {
        this.storage = storage;
        this.container = container;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        return storage.receiveEnergy((int) Math.min(maxReceive, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        return storage.extractEnergy((int) Math.min(maxExtract, Integer.MAX_VALUE), simulate);
    }

    @Override
    public long getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public long getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
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
