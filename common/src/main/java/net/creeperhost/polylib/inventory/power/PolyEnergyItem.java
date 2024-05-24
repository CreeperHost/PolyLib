package net.creeperhost.polylib.inventory.power;

import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 03/03/2024
 */
public interface PolyEnergyItem {

    IPolyEnergyStorage getEnergyStorage(ItemStack stack);

}
