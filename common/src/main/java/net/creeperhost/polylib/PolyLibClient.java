package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenHooks;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.development.DevelopmentTools;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
        if (Platform.isDevelopmentEnvironment())
        {
            DevelopmentTools.initClient();
        }
    }
}
