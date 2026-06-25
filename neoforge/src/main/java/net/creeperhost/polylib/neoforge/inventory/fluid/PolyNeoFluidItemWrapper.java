package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/**
 * Item variant of {@link PolyNeoFluidWrapper}.
 * <p>
 * After mutating the PolyLib storage, this wrapper updates the NeoForge
 * {@link ItemAccess} so automation receives the modified container stack.
 */
public class PolyNeoFluidItemWrapper extends PolyNeoFluidWrapper {
    private final IPolyFluidStorageItem storage;
    private final ItemAccess access;

    /**
     * @param storage PolyLib item fluid storage to expose to NeoForge
     * @param access item access to update after mutations
     */
    public PolyNeoFluidItemWrapper(IPolyFluidStorageItem storage, ItemAccess access) {
        super(storage);
        this.storage = storage;
        this.access = access;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
        PolyFluidStack stack = NeoPolyFluidWrapper.fromNeo(resource, NeoPolyFluidWrapper.mbToDroplets(amount));
        long inserted = storage.fill(stack, true);
        if (inserted > 0) {
            fluidJournal.updateSnapshots(transaction);
            PolyFluidStack last = storage.getFluid();
            int transferred = NeoPolyFluidWrapper.dropletsToMb(storage.fill(stack.copyWithAmount(inserted), false));
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            storage.setFluid(last);
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
        PolyFluidStack stack = NeoPolyFluidWrapper.fromNeo(resource, NeoPolyFluidWrapper.mbToDroplets(amount));
        PolyFluidStack extracted = storage.drain(stack, true);
        if (!extracted.isEmpty()) {
            fluidJournal.updateSnapshots(transaction);
            PolyFluidStack last = storage.getFluid();
            int transferred = NeoPolyFluidWrapper.dropletsToMb(storage.drain(extracted, false).getAmount());
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            storage.setFluid(last);
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

    public @NotNull ItemStack getContainer() {
        return storage.getContainer().isEmpty() ? access.getResource().toStack(access.getAmount()) : storage.getContainer();
    }
}
