package net.creeperhost.polylib.fabric.inventory.energy;

import net.creeperhost.polylib.inventory.energy.PolyEnergyContainer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import team.reborn.energy.api.EnergyStorage;

public class FabricItemEnergyContainer extends SnapshotParticipant<CompoundTag> implements EnergyStorage
{
    private final ContainerItemContext ctx;
    private final PolyEnergyContainer container;

    public FabricItemEnergyContainer(ContainerItemContext ctx, PolyEnergyContainer container)
    {
        this.ctx = ctx;
        CompoundTag nbt = ctx.getItemVariant().getNbt();
        if (nbt != null) container.deserialize(nbt);
        this.container = container;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction)
    {
        this.updateSnapshots(transaction);
        long inserted = container.insertEnergy(maxAmount, false);
        setChanged(transaction);
        return inserted;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction)
    {
        this.updateSnapshots(transaction);
        long l = container.extractEnergy(maxAmount, false);
        setChanged(transaction);
        return l;
    }

    @Override
    public long getAmount()
    {
        return container.getStoredEnergy();
    }

    @Override
    public long getCapacity()
    {
        return container.getMaxCapacity();
    }

    public void setChanged(TransactionContext transaction)
    {
        ItemStack stack = ctx.getItemVariant().toStack();
        this.container.serialize(stack.getOrCreateTag());
        ctx.exchange(ItemVariant.of(stack), ctx.getAmount(), transaction);
    }

    @Override
    protected CompoundTag createSnapshot()
    {
        return this.container.serialize(new CompoundTag());
    }

    @Override
    protected void readSnapshot(CompoundTag snapshot)
    {
        this.container.deserialize(snapshot);
    }
}
