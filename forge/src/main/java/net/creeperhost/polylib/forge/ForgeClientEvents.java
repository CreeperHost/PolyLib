package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeClientEvents
{
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::renderWorldLastEvent);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeClientEvents::registerReloadListeners);
    }

    private static void renderWorldLastEvent(RenderLevelStageEvent event)
    {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            ClientRenderEvents.LAST.invoker().onRenderLastEvent(event.getPoseStack());
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(PolyTextures.getAtlasHolder());
    }
}
