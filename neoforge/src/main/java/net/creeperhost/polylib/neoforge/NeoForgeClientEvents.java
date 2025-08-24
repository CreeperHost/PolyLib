package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientEvents {
    public static void init(IEventBus iEventBus) {
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::renderWorldLastEvent);

        iEventBus.addListener(NeoForgeClientEvents::registerReloadListeners);
    }

    private static void renderWorldLastEvent(RenderLevelStageEvent.AfterBlockEntities event) {
        ClientRenderEvents.LAST.invoker().onRenderLastEvent(event.getPoseStack());
    }

    private static void registerReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "texture_listener"), PolyTextures.getAtlasHolder());
    }
}
