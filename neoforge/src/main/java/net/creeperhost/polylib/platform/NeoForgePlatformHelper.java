package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.neoforge.inventory.fluid.NeoFluidManager;
import net.creeperhost.polylib.neoforge.inventory.power.NeoEnergyManager;
import net.creeperhost.polylib.platform.services.IPlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    private static final EnergyManager ENERGY_MANAGER = new NeoEnergyManager();
    private static final FluidManager FLUID_MANAGER = new NeoFluidManager();

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient()
    {
        return FMLEnvironment.getDist() == Dist.CLIENT;
    }

    @Override
    public EnergyManager getEnergyManager() {
        return ENERGY_MANAGER;
    }

    @Override
    public FluidManager getFluidManager() {
        return FLUID_MANAGER;
    }
}
