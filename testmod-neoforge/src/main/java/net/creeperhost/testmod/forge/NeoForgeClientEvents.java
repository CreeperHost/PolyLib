package net.creeperhost.testmod.forge;

import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class NeoForgeClientEvents
{
    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(NeoForgeClientEvents::registerReloadListeners);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event)
    {
        var testGui = Commands.literal("modular_gui_test").executes(context -> {
            Minecraft.getInstance().setScreen(new ModularGuiScreen(new ModularGuiTest()));
            return 0;
        });
        event.getDispatcher().register(testGui);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(TestModTextures.getAtlasHolder());
    }
}
