package net.creeperhost.polylib.inventory.power;

import net.minecraft.world.item.ItemStack;

/**
 * Created by brandon3055 on 03/03/2024
 */
public interface PolyEnergyItem {

    //TODO This should always return a IPolyEnergyStorageItem, Return type should be changed to IPolyEnergyStorageItem in next update.
    IPolyEnergyStorage getEnergyStorage(ItemStack stack);

}
