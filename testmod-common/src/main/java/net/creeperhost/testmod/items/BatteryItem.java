package net.creeperhost.testmod.items;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.creeperhost.polylib.inventory.power.PolyItemEnergyStorage;
import net.minecraft.world.item.ItemStack;

public class BatteryItem extends ItemPowered {
    private static final int MAX_ENERGY = 100000;
    private static final int BUFFER = 100;

    public BatteryItem(Properties properties) {
        super(properties);
    }

    @Override
    public IPolyEnergyStorageItem getEnergyStorage(ItemStack stack) {
        return new PolyItemEnergyStorage(stack, MAX_ENERGY, BUFFER);
    }
}
