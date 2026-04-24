package net.creeperhost.polylib.init;

import net.creeperhost.polylib.event.events.server.PolyChunkEvents;
import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.creeperhost.polylib.event.events.server.PolyServerTickEvents;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;

public class InternalEventListener
{
    public static void init()
    {
        PolyChunkEvents.CHUNK_LOAD.register(MultiblockRegistry::onChunkLoaded);
        PolyLevelEvents.LEVEL_LOAD.register(MultiblockRegistry::onWorldUnloaded);
        PolyServerTickEvents.LEVEL_TICK_START.register(MultiblockRegistry::tickStart);
    }
}
