package net.creeperhost.polylib.neoforge.inventory.energy;

import net.creeperhost.polylib.Serializable;
import net.creeperhost.polylib.neoforge.AutoSerializable;
import net.creeperhost.polylib.inventory.energy.PolyEnergyContainer;
import net.creeperhost.polylib.util.Updatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
//import net.neoforged.neoforge.common.capabilities.Capabilities;
//import net.neoforged.neoforge.common.capabilities.Capability;
//import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
//import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public record NeoForgeEnergyContainer<T extends PolyEnergyContainer & Updatable<BlockEntity>>(T container, BlockEntity entity) implements IEnergyStorage, AutoSerializable
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

    @Override
    public Serializable getSerializable()
    {
        return container;
    }

}