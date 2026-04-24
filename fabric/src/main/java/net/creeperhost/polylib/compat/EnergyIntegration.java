package net.creeperhost.polylib.compat;

import net.creeperhost.polylib.fabric.inventory.energy.FabricEnergyManager;
import net.creeperhost.polylib.fabric.inventory.energy.PolyFabricEnergyItemWrapper;
import net.creeperhost.polylib.fabric.inventory.energy.PolyFabricEnergyWrapper;
import net.creeperhost.polylib.inventory.power.*;
import team.reborn.energy.api.EnergyStorage;

public class EnergyIntegration {
    public static void registerEnergy() {
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof PolyEnergyItem item) {
                if (item.getEnergyStorage(itemStack) instanceof IPolyEnergyStorageItem storage){
                    return new PolyFabricEnergyItemWrapper(storage, context);
                }
            }
            return null;
        });

        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyEnergyBlock energyBlock) {
                IPolyEnergyStorage storage = energyBlock.getEnergyStorage(context);
                return storage == null ? null : new PolyFabricEnergyWrapper(storage);
            }
            return null;
        });
    }

    public static EnergyManager getEnergyManager() {
        return new FabricEnergyManager();
    }

}
