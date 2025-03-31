package net.creeperhost.testmod;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.init.TestItems;
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
        TestItems.CREATIVE_MODE_TABS.register();
        TestItems.ITEMS.register();
        TestContainers.CONTAINERS.register();
        PolyLib.initPolyItemData();

        if(Platform.getEnv() == EnvType.CLIENT)
        {
            TestModClient.init();
        }
    }
}
