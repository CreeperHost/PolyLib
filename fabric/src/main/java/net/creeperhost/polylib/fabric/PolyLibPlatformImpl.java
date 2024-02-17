package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.fabric.inventory.fluid.FabricFluidManager;
import net.creeperhost.polylib.inventory.fluid.PlatformFluidManager;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final PlatformFluidManager FLUID_MANAGER = new FabricFluidManager();

    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static PlatformFluidManager getFluidManager()
    {
        return FLUID_MANAGER;
    }
}
