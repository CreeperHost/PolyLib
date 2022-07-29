package net.creeperhost.testmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMod
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        LOGGER.info("Hi I'm " + MOD_ID);
    }
}
