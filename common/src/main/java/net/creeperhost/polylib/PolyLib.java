package net.creeperhost.polylib;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.client.modulargui.lib.CursorHelper;
import net.creeperhost.polylib.mulitblock.multiblockevents.MultiblockClientTickHandler;
import net.creeperhost.polylib.mulitblock.multiblockevents.MultiblockServerTickHandler;
import net.fabricmc.api.EnvType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PolyLib
{
    public static final String MOD_ID = "polylib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        if(Platform.getEnv() == EnvType.CLIENT)
        {
            MultiblockClientTickHandler.onClientTick();
            CursorHelper.init();
        }
        MultiblockServerTickHandler.onWorldTick();
    }
}
