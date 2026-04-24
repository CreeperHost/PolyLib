package net.creeperhost.polylib;

import net.creeperhost.polylib.event.events.client.PolyRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class FabricEventsClient {
    public static void init()
    {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeExtract(screen).register((screen1, graphics, mouseX, mouseY, tickProgress) -> {
                PolyRenderEvents.RENDER_GUI_PRE.invoker().onRenderGui(graphics, tickProgress);
            });
            ScreenEvents.afterExtract(screen).register((screen1, graphics, mouseX, mouseY, tickProgress) -> {
                PolyRenderEvents.RENDER_GUI_POST.invoker().onRenderGui(graphics, tickProgress);
            });
        });
    }
}
