package net.creeperhost.polylib.fabric.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 26/05/2024
 */
public class PolyFabricEnergyItemWrapper extends PolyFabricEnergyWrapper {
    private final IPolyEnergyStorageItem storage;
    private final ContainerItemContext itemContext;

    public PolyFabricEnergyItemWrapper(IPolyEnergyStorageItem storage, ContainerItemContext itemContext) {
        super(storage);
        this.storage = storage;
        this.itemContext = itemContext;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        long insertedAmount = storage.receiveEnergy(maxAmount, true);
        if (insertedAmount > 0) {
            updateSnapshots(transaction);
            long last = storage.getEnergyStored();
            long transferred = storage.receiveEnergy(maxAmount, false);
            if (tryUpdateItem(storage.getContainer(), transaction)){
                return transferred;
            }
            //If item update fails revert the transfer.
            storage.modifyEnergyStored(last - storage.getEnergyStored());
        }
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long extractedAmount = storage.extractEnergy(maxAmount, true);
        if (extractedAmount > 0) {
            updateSnapshots(transaction);
            long last = storage.getEnergyStored();
            long transferred = storage.extractEnergy(maxAmount, false);
            if (tryUpdateItem(storage.getContainer(), transaction)){
                return transferred;
            }
            //If item update fails revert the transfer.
            storage.modifyEnergyStored(last - storage.getEnergyStored());
        }
        return 0;
    }

    private boolean tryUpdateItem(ItemStack newStack, TransactionContext transaction) {
        ItemVariant newVariant = ItemVariant.of(newStack);
        long count = itemContext.getAmount();
        try (Transaction nested = transaction.openNested()) {
            if (itemContext.extract(itemContext.getItemVariant(), count, nested) == count && itemContext.insert(newVariant, newStack.getCount(), nested) == newStack.getCount()) {
                nested.commit();
                return true;
            }
        }
        return true;
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
}
