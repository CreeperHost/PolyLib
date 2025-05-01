package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.inventory.fluid.FabricFluidManager;
import net.creeperhost.polylib.fabric.inventory.power.FabricEnergyManager;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final FluidManager FLUID_MANAGER = new FabricFluidManager();
    private static final EnergyManager ENERGY_MANAGER = new FabricEnergyManager();

    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean isEnergyContainer(BlockEntity block, Direction direction)
    {
        if(Platform.isModLoaded("team_reborn_energy"))
        {
            return TeamRebornEnergyCompat.isEnergyContainer(block, direction);
        }
        PolyLib.LOGGER.info("team_reborn_energy is not loaded, Power systems that use polylib will not work");
        return false;
    }

    public static FluidManager getFluidManager() {
        if(Platform.isModLoaded("team_reborn_energy"))
        {
            return FLUID_MANAGER;
        }
        return null;
    }

    public static EnergyManager getEnergyManager() {
        if(Platform.isModLoaded("team_reborn_energy")) {
            return ENERGY_MANAGER;
        }
        return null;
    }
}
