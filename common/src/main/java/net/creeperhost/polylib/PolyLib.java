package net.creeperhost.polylib;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.core.PlayerTickEventHandler;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
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
            PolyLibClient.init();
        }

        PlayerTickEventHandler.init();

        TickEvent.SERVER_LEVEL_PRE.register(MultiblockRegistry::tickStart);
        ChunkEvents.CHUNK_LOAD_EVENT.register(MultiblockRegistry::onChunkLoaded);
        ChunkEvents.CHUNK_UNLOAD_EVENT.register((level, chunk) -> MultiblockRegistry.onWorldUnloaded(level));
    }
}
