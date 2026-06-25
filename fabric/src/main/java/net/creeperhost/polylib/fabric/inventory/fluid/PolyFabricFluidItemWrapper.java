package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

/**
 * Item variant of {@link PolyFabricFluidWrapper}.
 * <p>
 * After mutating the PolyLib storage, this wrapper updates the Fabric item
 * context so callers receive the modified container stack.
 */
public class PolyFabricFluidItemWrapper extends PolyFabricFluidWrapper {
    private final IPolyFluidStorageItem storage;
    private final ContainerItemContext itemContext;

    /**
     * @param storage PolyLib item fluid storage to expose to Fabric
     * @param itemContext Fabric item context to update after mutations
     */
    public PolyFabricFluidItemWrapper(IPolyFluidStorageItem storage, ContainerItemContext itemContext) {
        super(storage);
        this.storage = storage;
        this.itemContext = itemContext;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        PolyFluidStack stack = FabricPolyFluidWrapper.fromFabric(resource, maxAmount);
        long inserted = storage.fill(stack, true);
        if (inserted > 0) {
            updateSnapshots(transaction);
            PolyFluidStack last = storage.getFluid();
            long transferred = storage.fill(stack.copyWithAmount(inserted), false);
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            storage.setFluid(last);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        PolyFluidStack stack = FabricPolyFluidWrapper.fromFabric(resource, maxAmount);
        PolyFluidStack extracted = storage.drain(stack, true);
        if (!extracted.isEmpty()) {
            updateSnapshots(transaction);
            PolyFluidStack last = storage.getFluid();
            long transferred = storage.drain(extracted, false).getAmount();
            if (tryUpdateItem(storage.getContainer(), transaction)) {
                return transferred;
            }
            storage.setFluid(last);
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

    public ItemStack getContainer() {
        return storage.getContainer();
    }
}
