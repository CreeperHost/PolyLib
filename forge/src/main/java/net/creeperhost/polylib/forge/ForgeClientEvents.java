package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.TestGui;
import net.creeperhost.polylib.client.modulargui.sprite.GuiTextures;
import net.creeperhost.polylib.client.modulargui.sprite.ModAtlasHolder;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeClientEvents
{
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::renderWorldLastEvent);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::registerClientCommands);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeClientEvents::registerReloadListeners);
    }

    private static void renderWorldLastEvent(RenderLevelStageEvent event)
    {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            ClientRenderEvents.LAST.invoker().onRenderLastEvent(event.getPoseStack());
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event)
    {
        var testGui = Commands.literal("poly_test_gui").executes(context -> {
            Minecraft.getInstance().setScreen(new ModularGuiScreen(new TestGui()));
            return 0;
        });
        event.getDispatcher().register(testGui);
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(GuiTextures.getAtlasHolder());
    }
}
