package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLibPlatform;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void registerDefaultGenerators(DataGenerator dataGenerator)
    {
    }
}
