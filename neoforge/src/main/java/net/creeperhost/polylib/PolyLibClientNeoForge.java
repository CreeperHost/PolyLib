package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.nodegraph.render.BezierWirePiPRenderer;
import net.creeperhost.polylib.client.modulargui.nodegraph.render.BezierWireRenderState;
import net.creeperhost.polylib.client.modulargui.nodegraph.render.BezierWireUniform;
import net.creeperhost.polylib.client.modulargui.nodegraph.render.NodeCanvasRenderPipelines;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

public class PolyLibClientNeoForge
{
    public static void init(IEventBus eventBus)
    {
        eventBus.addListener(PolyLibClientNeoForge::atlasStitched);
        eventBus.addListener(PolyLibClientNeoForge::registerTextureAtlas);
        // Node canvas GPU bezier wires
        eventBus.addListener(PolyLibClientNeoForge::registerNodeCanvasPipelines);
        eventBus.addListener(PolyLibClientNeoForge::registerNodeCanvasPiPs);
        NeoForge.EVENT_BUS.addListener(PolyLibClientNeoForge::onRenderFrameEnd);
    }

    private static void registerTextureAtlas(RegisterTextureAtlasesEvent event) {
        AtlasManager.AtlasConfig config = new AtlasManager.AtlasConfig(PolyTextures.TEXTURE_ID, PolyTextures.DEFINITION_LOCATION, false);
        event.register(config);
    }

    private static void atlasStitched(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(PolyTextures.TEXTURE_ID)) {
            PolyTextures.setAtlas(event.getAtlas());
        }
    }

    private static void registerNodeCanvasPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(NodeCanvasRenderPipelines.BEZIER_WIRE);
    }

    private static void registerNodeCanvasPiPs(RegisterPictureInPictureRenderersEvent event) {
        event.register(BezierWireRenderState.class, BezierWirePiPRenderer::new);
    }

    private static void onRenderFrameEnd(RenderFrameEvent.Post event) {
        BezierWireUniform.STORAGE.get().endFrame();
    }
}

