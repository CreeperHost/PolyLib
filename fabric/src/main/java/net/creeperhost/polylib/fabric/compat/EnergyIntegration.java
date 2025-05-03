package net.creeperhost.polylib.fabric.compat;

import net.creeperhost.polylib.fabric.inventory.power.FabricEnergyManager;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyItemWrapper;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyWrapper;
import net.creeperhost.polylib.inventory.power.*;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 02/05/2025
 */
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
