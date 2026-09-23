package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

import java.util.Collections;
import java.util.Iterator;

/**
 * Exposes an {@link IPolyFluidStorage} as Fabric Transfer fluid storage.
 * <p>
 * Fabric transactions are backed by snapshots of the PolyLib storage contents.
 */
public class PolyFabricFluidWrapper extends SnapshotParticipant<PolyFluidStack> implements Storage<FluidVariant> {
    protected final IPolyFluidStorage storage;

    /**
     * @param storage PolyLib fluid storage to expose to Fabric
     */
    public PolyFabricFluidWrapper(IPolyFluidStorage storage) {
        this.storage = storage;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        PolyFluidStack stack = FabricPolyFluidWrapper.fromFabric(resource, maxAmount);
        long inserted = storage.fill(stack, true);
        if (inserted > 0) {
            updateSnapshots(transaction);
            return storage.fill(stack.copyWithAmount(inserted), false);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        PolyFluidStack stack = FabricPolyFluidWrapper.fromFabric(resource, maxAmount);
        PolyFluidStack extracted = storage.drain(stack, true);
        if (!extracted.isEmpty()) {
            updateSnapshots(transaction);
            return storage.drain(extracted, false).getAmount();
        }
        return 0;
    }

    @Override
    public boolean supportsInsertion() {
        return storage.canFill(storage.getFluid().isEmpty() ? new PolyFluidStack(net.minecraft.world.level.material.Fluids.WATER, 1) : storage.getFluid());
    }

    @Override
    public boolean supportsExtraction() {
        return storage.canDrain(storage.getFluid());
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return Collections.<StorageView<FluidVariant>>singleton(new View()).iterator();
    }

    @Override
    protected PolyFluidStack createSnapshot() {
        return storage.getFluid().copy();
    }

    @Override
    protected void readSnapshot(PolyFluidStack snapshot) {
        storage.setFluid(snapshot);
    }

    /**
     * Single Fabric storage view backed by the wrapped PolyLib storage.
     */
    protected class View implements StorageView<FluidVariant> {
        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            return PolyFabricFluidWrapper.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return storage.getFluid().isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            PolyFluidStack fluid = storage.getFluid();
            return fluid.isEmpty() ? FluidVariant.blank() : FabricPolyFluidWrapper.toFabric(fluid);
        }

        @Override
        public long getAmount() {
            return storage.getFluid().getAmount();
        }

        @Override
        public long getCapacity() {
            return storage.getCapacity();
        }
    }
}
