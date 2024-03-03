package net.creeperhost.polylib.inventory.energy;

import net.creeperhost.polylib.util.Updatable;
import net.minecraft.world.item.ItemStack;

@Deprecated
public interface PolyEnergyItem<T extends PolyEnergyContainer & Updatable<ItemStack>>
{
    T getEnergyStorage(ItemStack holder);
}
