package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyScreens;
import net.creeperhost.testmod.blocks.inventorytestblock.ScreenInventoryTest;

public class TestScreens
{
    public static void init()
    {
        PolyScreens.register(TestContainers.TEST_INVENTORY_CONTAINER, ScreenInventoryTest::create);
    }
}
