package net.creeperhost.polylib;

import net.creeperhost.polylib.init.InternalEventListenerClient;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapConfig;

public class PolyLibClient
{
    public static ConfigBuilder chunkMapConfigBuilder;
    public static PolyChunkMapConfig chunkMapConfig;

    public static void init()
    {
        InternalEventListenerClient.init();

        Constants.LOG.info("Registering PolyLib ChunkMap Client Config");
        chunkMapConfigBuilder = new ConfigBuilder(Constants.MOD_ID + "-chunkmap.json", Services.PLATFORM.getConfigFolder().resolve(Constants.MOD_ID + "-chunkmap.json"), PolyChunkMapConfig.class);
        chunkMapConfig = (PolyChunkMapConfig) chunkMapConfigBuilder.getConfigData();
        
        net.creeperhost.polylib.chunkmap.client.PolyChunkMapOverlay.init();
        // Chunk-map: clear client cache on disconnect

        net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents.CLIENT_PLAY_DISCONNECT
                .register((handler, client) -> net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient.reset());

        // Removed default ChatScreen injection for FloatingChatWindow to allow custom UI integrations.
    }


    public static Player getClientPlayer()
    {
        if (Services.PLATFORM.isClient())
        {
            return _getClientPlayer();
        }
        return null;
    }

    private static Player _getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
