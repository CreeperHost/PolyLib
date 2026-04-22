package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(ModularGuiInjector::tick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ModularGuiInjector.initPost(screen);
            ScreenEvents.afterExtract(screen).register(ModularGuiInjector::renderPost);
        });
    }
}
