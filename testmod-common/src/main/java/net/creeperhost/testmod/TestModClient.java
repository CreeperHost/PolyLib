package net.creeperhost.testmod;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.testmod.client.PlacementRenderer;
import net.creeperhost.testmod.client.gui.TestGui;
import net.creeperhost.testmod.init.TestScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Created by brandon3055 on 10/03/2025
 */
public class TestModClient {
    public static void init()
    {
        ClientLifecycleEvent.CLIENT_SETUP.register(instance -> TestScreens.init());
        ClientRenderEvents.LAST.register(PlacementRenderer::render);

        ClientGuiEvent.INIT_POST.register((screen, access) ->
        {
            if(screen instanceof TitleScreen titleScreen)
            {
                Button debugScreen = Button.builder(Component.literal("TestMod test screen"), button ->
                {
                    Minecraft.getInstance().setScreen(new TestGui());
                }).pos((titleScreen.width / 2) - 80, 40).build();

                List<GuiEventListener> children = (List<GuiEventListener>) screen.children();

                titleScreen.renderables.add(debugScreen);
                children.add(debugScreen);
            }
        });
    }
}
