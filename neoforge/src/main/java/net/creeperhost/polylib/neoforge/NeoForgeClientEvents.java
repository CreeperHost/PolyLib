package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientEvents
{
    public static void init(IEventBus iEventBus) {
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::renderWorldLastEvent);

        iEventBus.addListener(NeoForgeClientEvents::registerReloadListeners);
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
