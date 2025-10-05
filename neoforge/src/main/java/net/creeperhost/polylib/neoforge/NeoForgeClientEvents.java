package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.minecraft.client.resources.model.AtlasManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientEvents {
    public static void init(IEventBus iEventBus) {
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::renderWorldLastEvent);

        iEventBus.addListener(NeoForgeClientEvents::registerReloadListeners);
        iEventBus.addListener(NeoForgeClientEvents::AtlasStitched);
        iEventBus.addListener(NeoForgeClientEvents::registerTextureAtlas);
    }

    private static void registerTextureAtlas(RegisterTextureAtlasesEvent event) {
        AtlasManager.AtlasConfig config = new AtlasManager.AtlasConfig(PolyTextures.TEXTURE_ID, PolyTextures.DEFINITION_LOCATION, false);
        event.register(config);
    }

    private static void AtlasStitched(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(PolyTextures.TEXTURE_ID)) {
            PolyTextures.setAtlas(event.getAtlas());
        }
    }

    private static void renderWorldLastEvent(RenderLevelStageEvent.AfterBlockEntities event) {
        ClientRenderEvents.LAST.invoker().onRenderLastEvent(event.getPoseStack());
    }

    private static void registerReloadListeners(AddClientReloadListenersEvent event) {
//        event.addListener(ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "texture_listener"), PolyTextures.getAtlasHolder());
    }
}
