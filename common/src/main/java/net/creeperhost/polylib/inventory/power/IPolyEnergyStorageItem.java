package net.creeperhost.polylib.inventory.power;

import net.creeperhost.polylib.init.DataComps;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface IPolyEnergyStorageItem extends IPolyEnergyStorage {

    @NotNull
    ItemStack getContainer();

    default DataComponentType<Long> getEnergyComponent() {
        return DataComps.getItemEnergy();
    }
}
