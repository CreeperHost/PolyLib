package net.creeperhost.polylib.inventory.power;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface IPolyEnergyStorageItem extends IPolyEnergyStorage {

    @NotNull
    ItemStack getContainer();

}
