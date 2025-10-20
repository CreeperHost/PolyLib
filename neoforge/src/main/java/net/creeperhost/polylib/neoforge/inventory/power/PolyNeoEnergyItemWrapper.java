package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created by brandon3055 on 26/05/2024
 */
public class PolyNeoEnergyItemWrapper extends PolyNeoEnergyWrapper {
    private final IPolyEnergyStorageItem storage;
    private final ItemAccess access;

    public PolyNeoEnergyItemWrapper(IPolyEnergyStorageItem storage, ItemAccess access) {
        super(storage);
        this.storage = storage;
        this.access = access;
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        long insertedAmount = storage.receiveEnergy(maxAmount, true);
        if (insertedAmount > 0) {
            energyJournal.updateSnapshots(transaction);
            long last = storage.getEnergyStored();
            int transferred = (int) storage.receiveEnergy(maxAmount, false);
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            //If item update fails revert the transfer.
            storage.modifyEnergyStored(last - storage.getEnergyStored());
        }
        return 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        long extractedAmount = storage.extractEnergy(maxAmount, true);
        if (extractedAmount > 0) {
            energyJournal.updateSnapshots(transaction);
            long last = storage.getEnergyStored();
            int transferred = (int) storage.extractEnergy(maxAmount, false);
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            //If item update fails revert the transfer.
            storage.modifyEnergyStored(last - storage.getEnergyStored());
        }
        return 0;
    }

    private boolean tryUpdateItem(ItemStack newStack, TransactionContext transaction) {
        ItemResource newVariant = ItemResource.of(newStack);
        int count = access.getAmount();
        try (Transaction nested = Transaction.open(transaction)) {
            if (access.extract(access.getResource(), count, nested) == count && access.insert(newVariant, newStack.getCount(), nested) == newStack.getCount()) {
                nested.commit();
                return true;
            }
        }
        return true;
    }

}
