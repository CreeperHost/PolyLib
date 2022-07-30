package net.creeperhost.testmod.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.testmod.blocks.inventorytestblock.ScreenInventoryTestBlock;

public class TestScreens
{
    public static void init()
    {
        MenuRegistry.registerScreenFactory(TestContainers.TEST_INVENTORY_CONTAINER.get(), ScreenInventoryTestBlock::new);
    }
}
