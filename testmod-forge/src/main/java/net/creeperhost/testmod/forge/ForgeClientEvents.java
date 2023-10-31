package net.creeperhost.testmod.forge;

import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeClientEvents
{
    //This is a terrible but quick hack, do not do this in a real mod!
    private static boolean openTestUI = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::registerClientCommands);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::clientTick);
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeClientEvents::registerReloadListeners);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event)
    {
        var testGui = Commands.literal("modular_gui_test").executes(context -> {
            openTestUI = true;
            return 0;
        });
        event.getDispatcher().register(testGui);
    }

    private static void clientTick(TickEvent.ClientTickEvent event) {
        if (openTestUI) {
            openTestUI = false;
            Minecraft.getInstance().setScreen(new ModularGuiScreen(new ModularGuiTest()));
        }
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(TestModTextures.getUploader());
    }
}
