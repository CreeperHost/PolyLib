package net.creeperhost.polylib.inventory.energy;

import net.creeperhost.polylib.util.Updatable;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface PolyEnergyBlock<T extends PolyEnergyContainer & Updatable<BlockEntity>>
{
    T getEnergyStorage();
}
