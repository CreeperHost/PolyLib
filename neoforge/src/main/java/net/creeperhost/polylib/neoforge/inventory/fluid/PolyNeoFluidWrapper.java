package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes an {@link IPolyFluidStorage} as a NeoForge fluid {@link ResourceHandler}.
 * <p>
 * NeoForge transactions are backed by snapshots of the PolyLib storage contents.
 */
public class PolyNeoFluidWrapper implements ResourceHandler<FluidResource> {
    protected final IPolyFluidStorage storage;
    protected final FluidJournal fluidJournal = new FluidJournal();

    /**
     * @param storage PolyLib fluid storage to expose to NeoForge
     */
    public PolyNeoFluidWrapper(IPolyFluidStorage storage) {
        this.storage = storage;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return index == 0 ? NeoPolyFluidWrapper.toNeo(storage.getFluid()) : FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? NeoPolyFluidWrapper.dropletsToMb(storage.getFluid().getAmount()) : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return index == 0 && (resource.isEmpty() || storage.isFluidValid(NeoPolyFluidWrapper.fromNeo(resource, NeoPolyFluidWrapper.mbToDroplets(1))))
                ? NeoPolyFluidWrapper.dropletsToMb(storage.getCapacity())
                : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 0 && storage.isFluidValid(NeoPolyFluidWrapper.fromNeo(resource, NeoPolyFluidWrapper.mbToDroplets(1)));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
        PolyFluidStack stack = NeoPolyFluidWrapper.fromNeo(resource, NeoPolyFluidWrapper.mbToDroplets(amount));
        long inserted = storage.fill(stack, true);
        if (inserted > 0) {
            fluidJournal.updateSnapshots(transaction);
            return NeoPolyFluidWrapper.dropletsToMb(storage.fill(stack.copyWithAmount(inserted), false));
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
            return NeoPolyFluidWrapper.dropletsToMb(storage.drain(extracted, false).getAmount());
        }
        return 0;
    }

    /**
     * Transaction journal used to roll back PolyLib storage mutations.
     */
    protected class FluidJournal extends SnapshotJournal<PolyFluidStack> {
        @Override
        protected PolyFluidStack createSnapshot() {
            return storage.getFluid().copy();
        }

        @Override
        protected void revertToSnapshot(PolyFluidStack snapshot) {
            storage.setFluid(snapshot);
        }
    }
}
