package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.neoforge.inventory.energy.NeoForgeEnergyManager;
import net.creeperhost.polylib.neoforge.inventory.energy.NeoForgeItemEnergyManager;
import net.creeperhost.polylib.neoforge.inventory.fluid.NeoFluidManager;
import net.creeperhost.polylib.neoforge.inventory.power.NeoEnergyManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    private static final FluidManager FLUID_MANAGER = new NeoFluidManager();
    private static final EnergyManager ENERGY_MANAGER = new NeoEnergyManager();

    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    /**
     * This is our actual method to {@link PolyLibPlatform#isClientSide()}.
     */
    public static boolean isClientSide()
    {
        return FMLEnvironment.dist.isClient();
    }

    public static PlatformItemEnergyManager getItemEnergyManager(ItemStack stack)
    {
        return new NeoForgeItemEnergyManager(stack);
    }

    public static PlatformEnergyManager getBlockEnergyManager(BlockEntity entity, Direction direction)
    {
        return new NeoForgeEnergyManager(entity, direction);
    }

    public static boolean isEnergyItem(ItemStack stack)
    {
        return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }

    public static boolean isEnergyContainer(BlockEntity block, Direction direction)
    {
        return block.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, block.getBlockPos(), null) != null;
    }

    public static FluidManager getFluidManager() {
        return FLUID_MANAGER;
    }

    public static EnergyManager getEnergyManager() {
        return ENERGY_MANAGER;
    }
}
