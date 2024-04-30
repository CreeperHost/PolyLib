package net.creeperhost.polylib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class PolyLibPlatform
{
    @ExpectPlatform
    public static FluidManager getFluidManager() {
        throw new NotImplementedException("Fluid Manager not Implemented");
    }

    @ExpectPlatform
    public static EnergyManager getEnergyManager() {
        throw new NotImplementedException("Energy Manager not Implemented");
    }
}
