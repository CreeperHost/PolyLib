package net.creeperhost.polylib;

import net.creeperhost.polylib.compat.EnergyIntegration;
import net.creeperhost.polylib.fabric.inventory.energy.NullEnergyManager;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.ContainerStorageImpl;

public class FabricInventoryManager
{
    public static EnergyManager ENERGY_MANAGER = new NullEnergyManager();

    public static void init()
    {
        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyInventoryBlock invBlock) {
                return ContainerStorageImpl.of(invBlock.getContainer(context), context);
            }
            return null;
        });

        if (Services.PLATFORM.isModLoaded("team_reborn_energy")) {
            Constants.LOG.info("Detected team_reborn_energy, registering energy stuff!");
            ENERGY_MANAGER = EnergyIntegration.getEnergyManager();
            EnergyIntegration.registerEnergy();
        }
    }
}
