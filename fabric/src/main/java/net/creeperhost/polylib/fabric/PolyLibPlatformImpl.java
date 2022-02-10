package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.PolyLibPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    /**
     * This is our actual method to {@link PolyLibPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
