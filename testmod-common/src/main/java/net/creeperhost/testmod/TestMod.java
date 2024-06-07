package net.creeperhost.testmod;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.testmod.client.PlacementRenderer;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.init.TestItems;
import net.creeperhost.testmod.init.TestScreens;
import net.fabricmc.api.EnvType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMod
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger();


    public static void init()
    {
        TestBlocks.BLOCKS.register();
        TestBlocks.TILES_ENTITIES.register();
        TestItems.ITEMS.register();
        TestContainers.CONTAINERS.register();
        if(Platform.getEnv() == EnvType.CLIENT)
        {
            ClientLifecycleEvent.CLIENT_SETUP.register(instance -> TestScreens.init());
            ClientRenderEvents.LAST.register(PlacementRenderer::render);
        }
    }
}
