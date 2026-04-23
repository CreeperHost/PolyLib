package net.creeperhost.testmod;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.testmod.init.TestCreativeTabs;
import net.creeperhost.testmod.init.TestItems;

public class TestModCommon
{
    public static void init()
    {
        TestItems.init();
        TestCreativeTabs.init();

        if (Services.PLATFORM.isClient()) {
            TestModClientCommon.init();
        }
    }
}
