package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.forge.inventory.energy.ForgeEnergyManager;
import net.creeperhost.polylib.forge.inventory.energy.ForgeItemEnergyManager;
import net.creeperhost.polylib.inventory.energy.PlatformEnergyManager;
import net.creeperhost.polylib.inventory.energy.PlatformItemEnergyManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory()
    {
        return FMLPaths.CONFIGDIR.get();
    }

    public static PlatformItemEnergyManager getItemEnergyManager(ItemStack stack)
    {
        return new ForgeItemEnergyManager(stack);
    }

    public static PlatformEnergyManager getBlockEnergyManager(BlockEntity entity, Direction direction)
    {
        return new ForgeEnergyManager(entity, direction);
    }

    public static boolean isEnergyItem(ItemStack stack)
    {
        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    public static boolean isEnergyContainer(BlockEntity block, Direction direction)
    {
        return block.getCapability(ForgeCapabilities.ENERGY, direction).isPresent();
    }
}
