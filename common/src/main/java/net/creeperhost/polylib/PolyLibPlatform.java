package net.creeperhost.polylib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.creeperhost.polylib.inventory.energy.PlatformEnergyManager;
import net.creeperhost.polylib.inventory.energy.PlatformItemEnergyManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class PolyLibPlatform
{
    @ExpectPlatform
    public static Path getConfigDirectory()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isEnergyContainer(BlockEntity stack, @Nullable Direction direction)
    {
        throw new NotImplementedException("Energy item check not Implemented");
    }

    @ExpectPlatform
    public static boolean isEnergyItem(ItemStack stack)
    {
        throw new NotImplementedException("Energy item check not Implemented");
    }

    @ExpectPlatform
    public static PlatformEnergyManager getBlockEnergyManager(BlockEntity entity, @Nullable Direction direction)
    {
        throw new NotImplementedException("Block Entity Energy manager not implemented");
    }

    @ExpectPlatform
    public static PlatformItemEnergyManager getItemEnergyManager(ItemStack stack)
    {
        throw new NotImplementedException("Item Energy Manager not Implemented");
    }
}
