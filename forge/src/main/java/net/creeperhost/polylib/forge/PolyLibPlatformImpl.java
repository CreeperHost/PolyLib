package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.forge.inventory.fluid.ForgeFluidManager;
import net.creeperhost.polylib.forge.inventory.power.ForgeEnergyManager;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final FluidManager FLUID_MANAGER = new ForgeFluidManager();
    private static final EnergyManager ENERGY_MANAGER = new ForgeEnergyManager();

    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static FluidManager getFluidManager() {
        return FLUID_MANAGER;
    }

    public static EnergyManager getEnergyManager() {
        return ENERGY_MANAGER;
    }
}
