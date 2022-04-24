package net.creeperhost.polylib.mulitblock.multiblockevents;

import dev.architectury.event.events.common.TickEvent;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;

public class MultiblockServerTickHandler
{
    public static void onWorldTick()
    {
        TickEvent.SERVER_LEVEL_PRE.register(MultiblockRegistry::tickStart);
    }
}
