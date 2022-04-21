package net.creeperhost.polylib.mulitblock.multiblockevents;

import dev.architectury.event.events.client.ClientTickEvent;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;

public class MultiblockClientTickHandler
{
    public static void onClientTick()
    {
        ClientTickEvent.CLIENT_PRE.register(instance ->
        {
            MultiblockRegistry.tickStart(instance.level);
        });
    }
}
