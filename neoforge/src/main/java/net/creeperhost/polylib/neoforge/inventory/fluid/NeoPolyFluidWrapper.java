package net.creeperhost.polylib.neoforge.inventory.fluid;

import com.google.common.primitives.Ints;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Wraps a NeoForge fluid {@link ResourceHandler} as an {@link IPolyFluidStorage}.
 * <p>
 * NeoForge transfer fluid amounts are converted to PolyLib droplets at the API
 * boundary.
 */
public class NeoPolyFluidWrapper implements IPolyFluidStorage {
    protected final ResourceHandler<FluidResource> handler;

    /**
     * @param handler native NeoForge fluid handler to wrap
     */
    public NeoPolyFluidWrapper(ResourceHandler<FluidResource> handler) {
        this.handler = handler;
    }

    @Override
    public PolyFluidStack getFluid() {
        for (int i = 0; i < handler.size(); i++) {
            FluidResource resource = handler.getResource(i);
            long amount = handler.getAmountAsLong(i);
            if (!resource.isEmpty() && amount > 0) {
                return fromNeo(resource, amount);
            }
        }
        return PolyFluidStack.EMPTY;
    }

    @Override
    public long getCapacity() {
        long capacity = 0;
        for (int i = 0; i < handler.size(); i++) {
            FluidResource resource = handler.getResource(i);
            capacity += mbToDroplets(handler.getCapacityAsLong(i, resource.isEmpty() ? FluidResource.EMPTY : resource));
        }
        return capacity;
    }

    @Override
    public long fill(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty()) return 0;
        try (Transaction transaction = Transaction.open(null)) {
            int filled = handler.insert(toNeo(resource), dropletsToMb(resource.getAmount()), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return mbToDroplets(filled);
        }
    }

    @Override
    public PolyFluidStack drain(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty()) return PolyFluidStack.EMPTY;
        try (Transaction transaction = Transaction.open(null)) {
            int drained = handler.extract(toNeo(resource), dropletsToMb(resource.getAmount()), transaction);
            if (!simulate) {
                transaction.commit();
            }
            return resource.copyWithAmount(mbToDroplets(drained));
        }
    }

    @Override
    public PolyFluidStack drain(long maxDrain, boolean simulate) {
        PolyFluidStack fluid = getFluid();
        return fluid.isEmpty() ? PolyFluidStack.EMPTY : drain(fluid.copyWithAmount(maxDrain), simulate);
    }

    @Override
    public boolean canFill(PolyFluidStack resource) {
        return resource != null && !resource.isEmpty() && fill(resource.copyWithAmount(FluidManager.MILLIBUCKET), true) > 0;
    }

    @Override
    public boolean canDrain(PolyFluidStack resource) {
        return resource != null && !resource.isEmpty() && !drain(resource.copyWithAmount(FluidManager.MILLIBUCKET), true).isEmpty();
    }

    @Override
    public boolean isFluidValid(PolyFluidStack resource) {
        if (resource == null || resource.isEmpty()) return false;
        FluidResource fluid = toNeo(resource);
        for (int i = 0; i < handler.size(); i++) {
            if (handler.isValid(i, fluid)) {
                return true;
            }
        }
        return false;
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
     * Converts a PolyLib stack to a NeoForge fluid resource, dropping amount as
     * expected by {@link ResourceHandler}.
     */
    static FluidResource toNeo(PolyFluidStack stack) {
        if (stack == null || stack.isEmpty()) {
            return FluidResource.EMPTY;
        }
        return FluidResource.of(stack.getFluid(), stack.getComponents());
    }

    /**
     * Converts a NeoForge fluid resource and amount to a PolyLib fluid stack.
     */
    static PolyFluidStack fromNeo(FluidResource resource, long amount) {
        return resource.isEmpty() || amount <= 0 ? PolyFluidStack.EMPTY : new PolyFluidStack(resource.getFluid(), mbToDroplets(amount), resource.getComponentsPatch());
    }

    /**
     * Converts droplets to NeoForge's millibucket-like transfer unit.
     */
    static int dropletsToMb(long droplets) {
        return Ints.saturatedCast(droplets / FluidManager.MILLIBUCKET);
    }

    /**
     * Converts NeoForge fluid units to droplets.
     */
    static long mbToDroplets(int mb) {
        return mb * FluidManager.MILLIBUCKET;
    }

    /**
     * Converts NeoForge fluid units to droplets.
     */
    static long mbToDroplets(long mb) {
        return mb * FluidManager.MILLIBUCKET;
    }
}
