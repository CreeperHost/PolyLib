package net.creeperhost.polylib.fabric.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Created by brandon3055 on 15/02/2024
 */
@SuppressWarnings ("UnstableApiUsage")
public class PolyFabricFluidWrapper extends SnapshotParticipant<ResourceAmount<FluidVariant>> implements SingleSlotStorage<FluidVariant> {
    private final PolyFluidHandler handler;
    private final int tank;

    public PolyFabricFluidWrapper(PolyFluidHandler handler, int tank) {
        this.handler = handler;
        this.tank = tank;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        FluidStack stack = FluidStack.create(resource.getFluid(), maxAmount);

        long insertedAmount = handler.fill(stack, true);
        if (insertedAmount > 0) {
            updateSnapshots(transaction);
            return handler.fill(stack, false);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        FluidStack stack = FluidStack.create(resource.getFluid(), maxAmount);

        long extractedAmount = handler.drain(stack, true).getAmount();
        if (extractedAmount > 0) {
            updateSnapshots(transaction);
            return handler.drain(stack, false).getAmount();
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return handler.getFluidInTank(tank).isEmpty();
    }

    @Override
    public FluidVariant getResource() {
        return FluidVariant.of(handler.getFluidInTank(tank).getFluid());
    }

    @Override
    public long getAmount() {
        return handler.getFluidInTank(tank).getAmount();
    }

    @Override
    public long getCapacity() {
        return handler.getTankCapacity(tank);
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot() {
        return new ResourceAmount<>(FluidVariant.of(handler.getFluidInTank(tank).getFluid()), handler.getFluidInTank(tank).getAmount());
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        handler._setFluidInTank(tank, FluidStack.create(snapshot.resource().getFluid(), snapshot.amount()));
    }
}