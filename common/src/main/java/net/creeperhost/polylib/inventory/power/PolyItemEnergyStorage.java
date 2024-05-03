package net.creeperhost.polylib.inventory.power;

import net.creeperhost.polylib.init.DataComps;
import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 03/03/2024
 */
public class PolyItemEnergyStorage extends PolyEnergyStorage {
    private final ItemStack stack;

    public PolyItemEnergyStorage(ItemStack stack, long capacity) {
        super(capacity);
        this.stack = stack;
        loadEnergy();
    }

    public PolyItemEnergyStorage(ItemStack stack, long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
        this.stack = stack;
        loadEnergy();
    }

    public PolyItemEnergyStorage(ItemStack stack, long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
        loadEnergy();
    }

    public PolyItemEnergyStorage(ItemStack stack, long capacity, long maxReceive, long maxExtract, Runnable changeListener) {
        super(capacity, maxReceive, maxExtract, changeListener);
        this.stack = stack;
        loadEnergy();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        saveEnergy();
    }

    private void loadEnergy() {
        energy = stack.getOrDefault(DataComps.ITEM_ENERGY.get(), 0L);
    }

    private void saveEnergy() {
        stack.set(DataComps.ITEM_ENERGY.get(), energy);
    }
}
