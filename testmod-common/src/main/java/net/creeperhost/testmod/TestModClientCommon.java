package net.creeperhost.testmod;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatMember;
import net.creeperhost.polylib.chat.ChatRouter;
import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.chat.client.FloatingChatWindow;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.creeperhost.testmod.init.TestClientEvents;
import net.creeperhost.testmod.init.TestDebugEntries;
import net.creeperhost.testmod.init.TestScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.UUID;
import static net.creeperhost.testmod.TestModCommon.LOGGER;

public class TestModClientCommon
{
    public static void init()
    {
        TestClientEvents.init();
        TestScreens.init();
        TestDebugEntries.init();
        ModularGuiInjector.registerInjection(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());

        // Register a test chat channel
        Identifier testChannelId = Identifier.fromNamespaceAndPath("testmod", "test_channel");
        ChatChannel testChannel = new ChatChannel(testChannelId, Component.literal("Test Channel"), true);
        ChatRouter.getInstance().registerChannel(testChannel);

        // Add some dummy members and messages
        testChannel.addMember(new ChatMember(UUID.randomUUID(), Component.literal("Test User"), null, true));
        testChannel.addMessage(RichChatMessage.create(Component.literal("Welcome to the test channel!"), Component.literal("System")));
        testChannel.addMessage(RichChatMessage.create(Component.literal("This is a draggable modular window."), Component.literal("System")));

        // KP_5 (Numpad 5) — open a FloatingChatWindow for every registered channel
        PolyInputEvents.INPUT_KEY.register((key, scanCode, action, modifiers) ->
        {
            if (action != 1) return; // press only
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) return;
            if (key == GLFW.GLFW_KEY_KP_5)
            {
                java.util.Collection<ChatChannel> channels = ChatRouter.getInstance().getActiveChannels();
                if (channels.isEmpty())
                {
                    LOGGER.warn("[TestMod] KP_5: no channels registered");
                    return;
                }
                LOGGER.info("[TestMod] KP_5: opening FloatingChatWindow for {} channel(s)", channels.size());
                mc.setScreen(new ModularGuiScreen(gui ->
                {
                    gui.initFullscreenGui();
                    int offset = 0;
                    for (ChatChannel ch : channels)
                    {
                        // Wire up a local loopback submit handler if none is set
                        ch.setSubmitHandler(text ->
                        {
                            Minecraft mc2 = Minecraft.getInstance();
                            String senderName = mc2.player != null ? mc2.player.getScoreboardName() : "Player";
                            ch.addMessage(RichChatMessage.create(
                                    net.minecraft.network.chat.Component.literal(text),
                                    net.minecraft.network.chat.Component.literal(senderName)));
                        });
                        FloatingChatWindow win = new FloatingChatWindow(gui.getRoot(), ch);
                        win.constrain(GeoParam.LEFT,   Constraint.literal(40 + offset * 20))
                           .constrain(GeoParam.TOP,    Constraint.literal(40 + offset * 20))
                           .constrain(GeoParam.WIDTH,  Constraint.literal(280))
                           .constrain(GeoParam.HEIGHT, Constraint.literal(200));
                        offset++;
                    }
                }));
            }
        });
    }
}
