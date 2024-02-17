package net.creeperhost.polylib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.inventory.fluid.PlatformFluidManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.file.Path;

public class PolyLibPlatform
{
    @ExpectPlatform
    public static Path getConfigDirectory()
    {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static PlatformFluidManager getFluidManager()
    {
        throw new NotImplementedException("Fluid Manager not Implemented");
    }
}
