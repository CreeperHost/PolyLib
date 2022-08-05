package net.creeperhost.testmod;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.creeperhost.polylib.generators.PolyDataGeneratorRegistryEvent;
import net.creeperhost.testmod.datagen.TestModelProvider;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.init.TestItems;
import net.creeperhost.testmod.init.TestScreens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMod
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger();


    public static void init()
    {
        PolyDataGeneratorRegistryEvent.REGISTER_EVENT.register(polyDataGenerator ->
        {
            LOGGER.info("data register event called");
            polyDataGenerator.addProvider(new TestModelProvider(polyDataGenerator));
        });
        LOGGER.info("Hi I'm " + MOD_ID);
        TestBlocks.BLOCKS.register();
        TestBlocks.TILES_ENTITIES.register();
        TestItems.ITEMS.register();
        TestContainers.CONTAINERS.register();
        ClientLifecycleEvent.CLIENT_SETUP.register(instance ->
        {
            TestScreens.init();
        });
    }
}
