package net.creeperhost.polylib;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import io.sentry.Sentry;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.init.DataComps;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.sentry.SentryRegistry;
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

        if (!Platform.isDevelopmentEnvironment()) {
            String version = Platform.getMod(MOD_ID).getVersion();
            SentryRegistry.registerSentryHandler(Constants.DSN, version, Constants.PACKAGE_PATH);
        }

        PolyLibNetwork.init();
        DataComps.DATA.register();
        TickEvent.SERVER_LEVEL_PRE.register(MultiblockRegistry::tickStart);
        ChunkEvents.CHUNK_LOAD_EVENT.register(MultiblockRegistry::onChunkLoaded);
        ChunkEvents.CHUNK_UNLOAD_EVENT.register((level, chunk) -> MultiblockRegistry.onWorldUnloaded(level));
    }
}
