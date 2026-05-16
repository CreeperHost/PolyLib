package net.creeperhost.testmod;

import net.creeperhost.polylib.accessibility.AccessibilityOptionsRegistry;
import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatMember;
import net.creeperhost.polylib.chat.ChatRouter;
import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.client.config.ConfigPanelRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.modulargui.nodegraph.NodeTypeRegistry;
import net.creeperhost.polylib.platform.Services;
import net.creeperhost.testmod.init.TestClientEvents;
import net.creeperhost.testmod.init.TestScreens;
import net.creeperhost.testmod.nodegraph.TestItemPassthroughNodeType;
import net.creeperhost.testmod.nodegraph.TestSignalSourceNodeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.util.UUID;

public class TestModClientCommon
{
    private static boolean reduceMotion = false;
    private static boolean highContrast = false;

    public static void init()
    {
        TestClientEvents.init();
        TestScreens.init();
        ModularGuiInjector.registerInjection(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());

        AccessibilityOptionsRegistry.register("testmod.demo", builder -> builder
            .category("testmod.accessibility.demo_category", "PolyLib Testmod")
            .toggle(
                "testmod.accessibility.reduce_motion", "Reduce Motion",
                () -> reduceMotion, v -> reduceMotion = v
            )
            .toggle(
                "testmod.accessibility.high_contrast", "High Contrast (Demo)",
                () -> highContrast, v -> highContrast = v
            )
        );

        // Fabric: register config screen here (NeoForge uses NeoForgeConfigHelper in TestModNeoForge)
        if (!Services.PLATFORM.getPlatformName().equalsIgnoreCase("NeoForge")) {
            ConfigPanelRegistry.register(
                "testmod",
                parent -> new ConfirmScreen(
                    confirmed -> Minecraft.getInstance().setScreen(parent),
                    Component.literal("Testmod"),
                    Component.literal("No config screen registered yet."),
                    Component.literal("OK"),
                    Component.empty()
                )
            );
        }

        // Register test node types for the node graph demo (PR21)
        NodeTypeRegistry.register(TestSignalSourceNodeType.INSTANCE);
        NodeTypeRegistry.register(TestItemPassthroughNodeType.INSTANCE);

        // Register a test chat channel
        Identifier testChannelId = Identifier.fromNamespaceAndPath("testmod", "test_channel");
        ChatChannel testChannel = new ChatChannel(testChannelId, Component.literal("Test Channel"), true);
        ChatRouter.getInstance().registerChannel(testChannel);

        // Add some dummy members and messages
        testChannel.addMember(new ChatMember(UUID.randomUUID(), Component.literal("Test User"), null, true));
        testChannel.addMessage(RichChatMessage.create(Component.literal("Welcome to the test channel!"), Component.literal("System")));
        testChannel.addMessage(RichChatMessage.create(Component.literal("This is a draggable modular window."), Component.literal("System")));
    }
}


public class TestModClientCommon
{
    public static void init()
    {
        TestClientEvents.init();
        TestScreens.init();
        ModularGuiInjector.registerInjection(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());
<<<<<<< HEAD
=======

        AccessibilityOptionsRegistry.register("testmod.demo", builder -> builder
            .category("testmod.accessibility.demo_category", "PolyLib Testmod")
            .toggle(
                "testmod.accessibility.reduce_motion", "Reduce Motion",
                () -> reduceMotion, v -> reduceMotion = v
            )
            .toggle(
                "testmod.accessibility.high_contrast", "High Contrast (Demo)",
                () -> highContrast, v -> highContrast = v
            )
        );

        // Fabric: register config screen here (NeoForge uses NeoForgeConfigHelper in TestModNeoForge)
        if (!Services.PLATFORM.getPlatformName().equalsIgnoreCase("NeoForge")) {
            ConfigPanelRegistry.register(
                "testmod",
                parent -> new ConfirmScreen(
                    confirmed -> Minecraft.getInstance().setScreen(parent),
                    Component.literal("Testmod"),
                    Component.literal("No config screen registered yet."),
                    Component.literal("OK"),
                    Component.empty()
                )
            );
        }

        // Register test node types for the node graph demo (PR21)
        NodeTypeRegistry.register(TestSignalSourceNodeType.INSTANCE);
        NodeTypeRegistry.register(TestItemPassthroughNodeType.INSTANCE);

        // Register a test chat channel
        Identifier testChannelId = Identifier.fromNamespaceAndPath("testmod", "test_channel");
        ChatChannel testChannel = new ChatChannel(testChannelId, Component.literal("Test Channel"), true);
        ChatRouter.getInstance().registerChannel(testChannel);
        
        // Add some dummy members and messages
        testChannel.addMember(new ChatMember(UUID.randomUUID(), Component.literal("Test User"), null, true));
        testChannel.addMessage(RichChatMessage.create(Component.literal("Welcome to the test channel!"), Component.literal("System")));
        testChannel.addMessage(RichChatMessage.create(Component.literal("This is a draggable modular window."), Component.literal("System")));
>>>>>>> 91f9240 (feat(testmod): add coverage for PR10,12,17-22 — mirror screen, fuzzy search, canvas, nodegraph, offline data, energy, chat commands)
    }
}
