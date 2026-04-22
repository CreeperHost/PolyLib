package net.creeperhost.polylib.testmod;

import net.creeperhost.polylib.platform.Services;

public class TestModCommon
{
    public static void init()
    {
        if (Services.PLATFORM.isClient()) {
            TestModClientCommon.init();
        }
    }
}
