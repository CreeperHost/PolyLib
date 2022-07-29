package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientTickEvent;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
    }
}
