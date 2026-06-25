package net.creeperhost.polylib;

import net.creeperhost.polylib.compat.EnergyIntegration;
import net.creeperhost.polylib.fabric.inventory.fluid.FabricFluidManager;
import net.creeperhost.polylib.fabric.inventory.fluid.PolyFabricFluidItemWrapper;
import net.creeperhost.polylib.fabric.inventory.fluid.PolyFabricFluidWrapper;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.fluid.PolyFluidItem;
import net.creeperhost.polylib.fabric.inventory.energy.NullEnergyManager;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.platform.Services;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.ContainerStorageImpl;

public class FabricInventoryManager
{
    public static EnergyManager ENERGY_MANAGER = new NullEnergyManager();
    public static FluidManager FLUID_MANAGER = new FabricFluidManager();

    public static void init()
    {
        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyInventoryBlock invBlock) {
                return ContainerStorageImpl.of(invBlock.getContainer(context), context);
            }
            return null;
        });

        FluidStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyFluidBlock fluidBlock) {
                IPolyFluidStorage storage = fluidBlock.getFluidStorage(context);
                return storage == null ? null : new PolyFabricFluidWrapper(storage);
            }
            return null;
        });

        FluidStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof PolyFluidItem item) {
                IPolyFluidStorage storage = item.getFluidStorage(itemStack);
                if (storage instanceof net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem itemStorage) {
                    return new PolyFabricFluidItemWrapper(itemStorage, context);
                }
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
