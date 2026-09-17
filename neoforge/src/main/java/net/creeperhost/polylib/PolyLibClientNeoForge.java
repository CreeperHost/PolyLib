package net.creeperhost.polylib;

import net.creeperhost.polylib.debug.neoforge.NeoForgeDebugBridge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.creeperhost.polylib.config.NeoForgeConfigHelper;

public class PolyLibClientNeoForge
{
    public static void registerConfigScreen(ModContainer container)
    {
        NeoForgeConfigHelper.register(container, PolylibCommon.configBuilder);
    }

    public static void init(IEventBus eventBus)
    {
        eventBus.addListener(NeoForgeDebugBridge::onRegisterDebugEntries);
    }
}
