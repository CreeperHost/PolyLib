package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;

public class PolyLibClientFabric
{
    public static void init()
    {
        PolyLibNetwork.initClient();

        ClientTickEvents.END_CLIENT_TICK.register(ModularGuiInjector::tick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ModularGuiInjector.initPost(screen);
            ScreenEvents.afterExtract(screen).register(ModularGuiInjector::renderPost);
            ScreenKeyboardEvents.afterKeyPress(screen).register((screen1, event)
                    -> ModularGuiInjector.keyPressed(Minecraft.getInstance(), screen1, event));
            ScreenKeyboardEvents.afterKeyRelease(screen).register((screen1, event)
                    -> ModularGuiInjector.keyReleased(Minecraft.getInstance(), screen1, event));
            ScreenMouseEvents.afterMouseClick(screen).register((screen1, event, consumed)
                    -> ModularGuiInjector.mouseClicked(Minecraft.getInstance(), screen1, event, consumed));
            ScreenMouseEvents.afterMouseRelease(screen).register((screen1, event, consumed)
                    -> ModularGuiInjector.mouseReleased(Minecraft.getInstance(), screen1, event));
            ScreenMouseEvents.afterMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount, consumed)
                    -> ModularGuiInjector.mouseScrolled(Minecraft.getInstance(), screen1, mouseX, mouseY, horizontalAmount, verticalAmount));
        });
    }
}
