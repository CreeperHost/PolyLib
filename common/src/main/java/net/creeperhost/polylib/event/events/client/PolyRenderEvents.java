package net.creeperhost.polylib.event.events.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public final class PolyRenderEvents
{
    /**
     * Fired after translucent particles are rendered in the world.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterTranslucentParticles}<br>
     * Fabric: {@code LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES}
     */
    public static final PolyEvent<RenderLevel> AFTER_TRANSLUCENT_PARTICLES = PolyEvent.create(
            handlers -> poseStack -> handlers.forEach(h -> h.onRender(poseStack)));

    /**
     * Fired after translucent blocks are rendered in the world.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterTranslucentBlocks}<br>
     * Fabric: {@code LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN}
     */
    public static final PolyEvent<RenderLevel> AFTER_TRANSLUCENT_BLOCKS = PolyEvent.create(
            handlers -> poseStack -> handlers.forEach(h -> h.onRender(poseStack)));

    /**
     * Fired after opaque blocks are rendered in the world.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterOpaqueBlocks}<br>
     * Fabric: {@code LevelRenderEvents.AFTER_OPAQUE_TERRAIN} (cast to {@code LevelRenderContext})
     */
    public static final PolyEvent<RenderLevel> AFTER_OPAQUE_BLOCKS = PolyEvent.create(
            handlers -> poseStack -> handlers.forEach(h -> h.onRender(poseStack)));

    /**
     * Fired before the HUD is rendered each frame.
     * <p>
     * NeoForge: {@code RenderGuiLayerEvent.Pre}<br>
     * Fabric: mixin into {@code Gui#renderHud}
     */
    public static final PolyEvent<RenderGui> RENDER_GUI_PRE = PolyEvent.create(
            handlers -> (graphics, partialTick) -> handlers.forEach(h -> h.onRenderGui(graphics, partialTick)));

    /**
     * Fired after the HUD has been rendered each frame.
     * <p>
     * NeoForge: {@code RenderGuiLayerEvent.Post}<br>
     * Fabric: mixin into {@code Gui#renderHud} at RETURN
     */
    public static final PolyEvent<RenderGui> RENDER_GUI_POST = PolyEvent.create(
            handlers -> (graphics, partialTick) -> handlers.forEach(h -> h.onRenderGui(graphics, partialTick)));

    /**
     * Fired before each individual HUD overlay layer is rendered.
     * Call {@link CancelContext#cancel()} to suppress this layer.
     * {@code layerId} identifies the specific overlay (e.g., hotbar, health).
     * <p>
     * NeoForge: {@code RenderGuiLayerEvent.Pre}<br>
     * Fabric: not natively supported
     */
    public static final PolyEvent<GuiOverlayPre> GUI_OVERLAY_PRE = PolyEvent.create(handlers -> (layerId, graphics, partialTick, ctx) ->
    {
        for (var h : handlers)
        {
            h.onGuiOverlayPre(layerId, graphics, partialTick, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after each individual HUD overlay layer has been rendered.
     * {@code layerId} identifies the specific overlay.
     * <p>
     * NeoForge: {@code RenderGuiLayerEvent.Post}<br>
     * Fabric: not natively supported
     */
    public static final PolyEvent<GuiOverlayPost> GUI_OVERLAY_POST = PolyEvent.create(
            handlers -> (layerId, graphics, partialTick) ->
                    handlers.forEach(h -> h.onGuiOverlayPost(layerId, graphics, partialTick)));

    private PolyRenderEvents()
    {
    }

    @FunctionalInterface
    public interface RenderLevel
    {
        void onRender(PoseStack poseStack);
    }

    @FunctionalInterface
    public interface RenderGui
    {
        void onRenderGui(GuiGraphicsExtractor graphics, float partialTick);
    }

    @FunctionalInterface
    public interface GuiOverlayPre
    {
        void onGuiOverlayPre(Identifier layerId, GuiGraphicsExtractor graphics, float partialTick, CancelContext ctx);
    }

    @FunctionalInterface
    public interface GuiOverlayPost
    {
        void onGuiOverlayPost(Identifier layerId, GuiGraphicsExtractor graphics, float partialTick);
    }
}
