package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.development.DevelopmentTools;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
        if (Platform.isDevelopmentEnvironment())
        {
            DevelopmentTools.initClient();
        }
    }
}
