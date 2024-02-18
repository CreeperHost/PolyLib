package net.creeperhost.polylib.fabric.inventory.fluid;

import com.google.common.collect.Iterables;
import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 17/02/2024
 */
public class FabricPolyFluidWrapper implements PolyFluidHandler {
    private final Storage<FluidVariant> storage;

    public FabricPolyFluidWrapper(Storage<FluidVariant> storage) {
        this.storage = storage;
    }

    @Override
    public int getTanks() {
        return Iterables.size(storage);
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        StorageView<FluidVariant> view = Iterables.get(storage, tank);
        return FluidStack.create(view.getResource().getFluid(), view.getAmount());
    }

    @Override
    public long getTankCapacity(int tank) {
        StorageView<FluidVariant> view = Iterables.get(storage, tank);
        return view.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        StorageView<FluidVariant> view = Iterables.get(storage, tank);
        return view.isResourceBlank() || view.getResource().getFluid().isSame(stack.getFluid());
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (!storage.supportsInsertion()) {
            return 0;
        }

        long inserted;
        try (Transaction transaction = Transaction.openOuter()) {
            inserted = storage.insert(FluidVariant.of(resource.getFluid()), resource.getAmount(), transaction);
            if (!simulate) {
                transaction.commit();
            }
        }

        return inserted;
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        if (!storage.supportsExtraction()) {
            return FluidStack.empty();
        }

        for (StorageView<FluidVariant> view : storage) {
            if (view.isResourceBlank() || view.getAmount() <= 0) continue;
            return drain(view.getResource(), maxDrain, simulate);
        }

        return FluidStack.empty();
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!storage.supportsExtraction()) {
            return FluidStack.empty();
        }

        return drain(FluidVariant.of(resource.getFluid()), resource.getAmount(), simulate);
    }

    private @NotNull FluidStack drain(FluidVariant variant, long amount, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()){
            long extracted = storage.extract(variant, amount, transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted > 0 ? FluidStack.create(variant.getFluid(), extracted) : FluidStack.empty();
        }
    }

    @Override
    public void _setFluidInTank(int tank, FluidStack fluidStack) {
        //No-op This should never be called.
    }
}
