package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * Wraps a Fabric Transfer fluid storage as an {@link IPolyFluidStorage}.
 */
public class FabricPolyFluidWrapper implements IPolyFluidStorage {
    protected final Storage<FluidVariant> storage;

    /**
     * @param storage native Fabric fluid storage to wrap
     */
    public FabricPolyFluidWrapper(Storage<FluidVariant> storage) {
        this.storage = storage;
    }

    @Override
    public PolyFluidStack getFluid() {
        for (StorageView<FluidVariant> view : storage.nonEmptyViews()) {
            return fromFabric(view.getResource(), view.getAmount());
        }
        return PolyFluidStack.EMPTY;
    }

    @Override
    public long getCapacity() {
        long capacity = 0;
        for (StorageView<FluidVariant> view : storage) {
            capacity += view.getCapacity();
        }
        return capacity;
    }

    @Override
    public long fill(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty()) return 0;
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(toFabric(resource), resource.getAmount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return inserted;
        }
    }

    @Override
    public PolyFluidStack drain(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty()) return PolyFluidStack.EMPTY;
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(toFabric(resource), resource.getAmount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return resource.copyWithAmount(extracted);
        }
    }

    @Override
    public PolyFluidStack drain(long maxDrain, boolean simulate) {
        PolyFluidStack fluid = getFluid();
        return fluid.isEmpty() ? PolyFluidStack.EMPTY : drain(fluid.copyWithAmount(maxDrain), simulate);
    }

    @Override
    public boolean canFill(PolyFluidStack resource) {
        return storage.supportsInsertion() && resource != null && !resource.isEmpty();
    }

    @Override
    public boolean canDrain(PolyFluidStack resource) {
        return storage.supportsExtraction() && resource != null && !resource.isEmpty();
    }

    @Override
    public boolean isFluidValid(PolyFluidStack resource) {
        return resource != null && !resource.isEmpty();
    }

    @Override
    public void setFluid(PolyFluidStack stack) {
        PolyFluidStack current = getFluid();
        if (!current.isEmpty()) {
            drain(current, false);
        }
        if (stack != null && !stack.isEmpty()) {
            fill(stack, false);
        }
    }

    /**
     * Converts a PolyLib fluid stack to a Fabric fluid variant.
     */
    static FluidVariant toFabric(PolyFluidStack stack) {
        return FluidVariant.of(stack.getFluid(), stack.getComponents());
    }

    /**
     * Converts a Fabric fluid variant and amount to a PolyLib fluid stack.
     */
    static PolyFluidStack fromFabric(FluidVariant variant, long amount) {
        return variant.isBlank() || amount <= 0 ? PolyFluidStack.EMPTY : new PolyFluidStack(variant.getFluid(), amount, variant.getComponentsPatch());
    }
}
