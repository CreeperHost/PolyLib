package net.creeperhost.polylib.neoforge.inventory.fluid;

import com.google.common.primitives.Ints;
import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 17/02/2024
 */
public class NeoPolyFluidWrapper implements PolyFluidHandler {

    private final ResourceHandler<FluidResource> storage;

    public NeoPolyFluidWrapper(ResourceHandler<FluidResource> storage) {
        this.storage = storage;
    }

    @Override
    public int getTanks() {
        return storage.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return FluidStack.create(storage.getResource(tank).getFluid(), storage.getAmountAsLong(tank));
    }

    @Override
    public long getTankCapacity(int tank) {
        return storage.getCapacityAsLong(tank, FluidResource.EMPTY);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return storage.isValid(tank, FluidResource.of(stack.getFluid(), stack.getPatch()));
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        long inserted;
        try (Transaction transaction = Transaction.open(null)) {
            inserted = storage.insert(FluidResource.of(resource.getFluid()), Ints.saturatedCast(resource.getAmount()), transaction);
            if (!simulate) {
                transaction.commit();
            }
        }

        return inserted;
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        if (storage.size() <= 0) {
            return FluidStack.empty();
        }

        for (int index = 0; index < storage.size(); index++) {
            FluidResource resource = storage.getResource(index);
            if (resource.isEmpty()) continue;
            return drain(resource, maxDrain, simulate);
        }

        return FluidStack.empty();
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        return drain(FluidResource.of(resource.getFluid()), resource.getAmount(), simulate);
    }

    private @NotNull FluidStack drain(FluidResource resource, long amount, boolean simulate) {
        try (Transaction transaction = Transaction.open(null)){
            long extracted = storage.extract(resource, Ints.saturatedCast(amount), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return extracted > 0 ? FluidStack.create(resource.getFluid(), extracted) : FluidStack.empty();
        }
    }

    @Override
    public void _setFluidInTank(int tank, FluidStack fluidStack) {
        //No-op This should never be called.
    }
}
