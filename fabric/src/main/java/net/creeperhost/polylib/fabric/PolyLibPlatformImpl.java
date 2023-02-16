package net.creeperhost.polylib.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    public static Path getConfigDirectory()
    {
        return FabricLoader.getInstance().getConfigDir();
    }
}
