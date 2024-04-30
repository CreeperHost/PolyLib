package net.creeperhost.polylib.neoforge.inventory.energy;

import net.creeperhost.polylib.util.Updatable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record NeoForgeItemEnergyContainer<T extends PolyEnergyContainer & Updatable<ItemStack>>(T container, ItemStack entity) implements IEnergyStorage
{
    @Override
    public int receiveEnergy(int maxAmount, boolean bl)
    {
        if (maxAmount <= 0) return 0;
        int inserted = (int) container.insertEnergy(Math.min(maxAmount, container.maxInsert()), bl);
        container.update(entity);
        return inserted;
    }

    @Override
    public int extractEnergy(int maxAmount, boolean bl)
    {
        if (maxAmount <= 0) return 0;
        int extracted = (int) container.extractEnergy(Math.min(maxAmount, container.maxExtract()), bl);
        container.update(entity);
        return extracted;
    }

    @Override
    public int getEnergyStored()
    {
        return (int) container.getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return (int) container.getMaxCapacity();
    }

    @Override
    public boolean canExtract()
    {
        return container.allowsExtraction();
    }

    @Override
    public boolean canReceive()
    {
        return container.allowsInsertion();
    }
}
