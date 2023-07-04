package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.fabric.inventory.energy.FabricEnergyManager;
import net.creeperhost.polylib.fabric.inventory.energy.FabricItemEnergyManager;
import net.creeperhost.polylib.inventory.energy.PlatformEnergyManager;
import net.creeperhost.polylib.inventory.energy.PlatformItemEnergyManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static PlatformItemEnergyManager getItemEnergyManager(ItemStack stack)
    {
        return new FabricItemEnergyManager(stack);
    }

    public static boolean isEnergyItem(ItemStack stack)
    {
        return EnergyStorageUtil.isEnergyStorage(stack);
    }

    public static PlatformEnergyManager getBlockEnergyManager(BlockEntity entity, Direction direction)
    {
        return new FabricEnergyManager(entity, direction);
    }

    public static boolean isEnergyContainer(BlockEntity block, Direction direction)
    {
        return EnergyStorage.SIDED.find(block.getLevel(), block.getBlockPos(), direction) != null;
    }
}
