package net.creeperhost.polylib.neoforge.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class PolyNeoFluidWrapper implements ResourceHandler<FluidResource> {

    private final PolyFluidHandler handler;

    public PolyNeoFluidWrapper(PolyFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public int size() {
        return handler.getTanks();
    }

    @Override
    public FluidResource getResource(int index) {
        return FluidResource.of(FluidStackHooksForge.toForge(handler.getFluidInTank(index)));
    }

    @Override
    public long getAmountAsLong(int index) {
        return (int) Math.min(Integer.MAX_VALUE, handler.getFluidInTank(index).getAmount());
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return (int) Math.min(Integer.MAX_VALUE, handler.getTankCapacity(index));
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return handler.isFluidValid(index, FluidStackHooksForge.fromForge(resource.toStack((int) FluidStackHooks.bucketAmount())));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        dev.architectury.fluid.FluidStack stack = FluidStack.create(resource.getFluid(), amount);

        long insertedAmount = handler.fill(stack, true);
        if (insertedAmount > 0) {
            //TODO what about reversion? Does the neo impl have a snapshot system that needs to be implemented here?
            return (int) handler.fill(stack, false);
        }
        return 0;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        FluidStack stack = FluidStack.create(resource.getFluid(), amount);

        long extractedAmount = handler.drain(stack, true).getAmount();
        if (extractedAmount > 0) {

            return (int) handler.drain(stack, false).getAmount();
        }
        return 0;
    }
}
