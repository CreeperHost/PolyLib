package net.creeperhost.testmod.forge;

import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeClientEvents
{
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::registerClientCommands);
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeClientEvents::registerReloadListeners);
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
