package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.fabric.inventory.fluid.FabricFluidManager;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final FluidManager FLUID_MANAGER = new FabricFluidManager();

    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static FluidManager getFluidManager() {
        return FLUID_MANAGER;
    }

    public static EnergyManager getEnergyManager() {
        return PolyLibFabric.ENERGY_MANAGER;
    }
}
