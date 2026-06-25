package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.FabricInventoryManager;
import net.creeperhost.polylib.PolyLibFabric;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper
{

    @Override
    public String getPlatformName()
    {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId)
    {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment()
    {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigFolder()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isClient()
    {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public EnergyManager getEnergyManager() {
        return FabricInventoryManager.ENERGY_MANAGER;
    }

    @Override
    public FluidManager getFluidManager() {
        return FabricInventoryManager.FLUID_MANAGER;
    }
}
