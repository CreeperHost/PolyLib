package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(ModularGuiInjector::tick);
    }
}
