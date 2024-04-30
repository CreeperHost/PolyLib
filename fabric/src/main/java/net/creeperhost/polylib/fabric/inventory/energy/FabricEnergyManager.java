package net.creeperhost.polylib.fabric.inventory.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

public record FabricEnergyManager(EnergyStorage energy) implements PlatformEnergyManager
{
    public FabricEnergyManager(BlockEntity entity, Direction direction)
    {
        this(EnergyStorage.SIDED.find(entity.getLevel(), entity.getBlockPos(), direction));
    }

    @Override
    public long getStoredEnergy()
    {
        return energy.getAmount();
    }

    @Override
    public long getCapacity()
    {
        return energy.getCapacity();
    }

    @Override
    public long extract(long amount, boolean simulate)
    {
        try (Transaction txn = Transaction.openOuter())
        {
            long extract = energy.extract(amount, txn);
            if (simulate) txn.abort();
            else txn.commit();
            return extract;
        }
    }

    @Override
    public long insert(long amount, boolean simulate)
    {
        try (Transaction txn = Transaction.openOuter())
        {
            long insert = energy.insert(amount, txn);
            if (simulate) txn.abort();
            else txn.commit();
            return insert;
        }
    }

    @Override
    public boolean supportsInsertion()
    {
        return energy.supportsInsertion();
    }

    @Override
    public boolean supportsExtraction()
    {
        return energy.supportsExtraction();
    }
}
