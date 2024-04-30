package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.fabric.inventory.energy.FabricItemEnergyManager;
import net.creeperhost.polylib.fabric.inventory.power.FabricEnergyManager;
import net.fabricmc.api.EnvType;
import net.creeperhost.polylib.fabric.inventory.fluid.FabricFluidManager;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

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
        return EnergyStorage.SIDED.find(block.getLevel(), block.getBlockPos(), direction) != null;
    }

    public static FluidManager getFluidManager() {
        return FLUID_MANAGER;
    }

    public static EnergyManager getEnergyManager() {
        return ENERGY_MANAGER;
    }
}
