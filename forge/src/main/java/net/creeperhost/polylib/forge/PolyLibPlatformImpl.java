package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.forge.inventory.fluid.ForgeFluidManager;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final FluidManager FLUID_MANAGER = new ForgeFluidManager();

    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static FluidManager getFluidManager()
    {
        return FLUID_MANAGER;
    }
}
