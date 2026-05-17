package net.creeperhost.polylib;

import net.creeperhost.polylib.debug.neoforge.NeoForgeDebugBridge;
import net.neoforged.bus.api.IEventBus;

public class PolyLibClientNeoForge
{
    public static void init(IEventBus eventBus)
    {
        eventBus.addListener(NeoForgeDebugBridge::onRegisterDebugEntries);
    }
}
