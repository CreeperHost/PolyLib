package net.creeperhost.polylib.inventory.energy.impl;

import net.creeperhost.polylib.inventory.energy.EnergySnapshot;
import net.creeperhost.polylib.inventory.energy.PolyEnergyContainer;
import net.creeperhost.polylib.util.Updatable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record WrappedItemEnergyContainer(ItemStack stack, PolyEnergyContainer container) implements PolyEnergyContainer, Updatable<ItemStack>
{
    public WrappedItemEnergyContainer
    {
        container.deserialize(stack.getOrCreateTag());
    }

    @Override
    public long insertEnergy(long energy, boolean simulate)
    {
        return container.insertEnergy(energy, simulate);
    }

    @Override
    public long extractEnergy(long energy, boolean simulate)
    {
        return container.extractEnergy(energy, simulate);
    }

    @Override
    public long internalInsert(long amount, boolean simulate)
    {
        long l = container.internalInsert(amount, simulate);
        if (!simulate) update(stack);
        return l;
    }

    @Override
    public long internalExtract(long amount, boolean simulate)
    {
        long extracted = container.internalExtract(amount, simulate);
        if (!simulate) update(stack);
        return extracted;
    }

    @Override
    public void setEnergy(long energy)
    {
        container.setEnergy(energy);
    }

    @Override
    public long getStoredEnergy()
    {
        return container.getStoredEnergy();
    }

    @Override
    public long getMaxCapacity()
    {
        return container.getMaxCapacity();
    }

    @Override
    public long maxInsert()
    {
        return container.maxInsert();
    }

    @Override
    public long maxExtract()
    {
        return container.maxExtract();
    }

    @Override
    public boolean allowsInsertion()
    {
        return container.allowsInsertion();
    }

    @Override
    public boolean allowsExtraction()
    {
        return container.allowsExtraction();
    }

    @Override
    public EnergySnapshot createSnapshot()
    {
        return container.createSnapshot();
    }

    @Override
    public void deserialize(CompoundTag nbt)
    {
        container.deserialize(nbt);
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt)
    {
        return container.serialize(nbt);
    }

    @Override
    public void update(ItemStack object)
    {
        container.serialize(object.getOrCreateTag());
    }

    @Override
    public void clearContent()
    {
        container.clearContent();
    }
}
