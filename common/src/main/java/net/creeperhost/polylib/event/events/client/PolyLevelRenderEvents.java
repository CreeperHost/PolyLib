package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;

/**
 * Client-side level rendering stage events.
 *
 * <p>All SAMs receive the {@link Minecraft} instance as their sole typed parameter;
 * loader-specific context objects (PoseStack, Camera, etc.) are intentionally
 * omitted to keep the API loader-agnostic.
 *
 * <p>Stages with <b>no Fabric native equivalent</b> are fired only on NeoForge
 * (NeoForge-primary). Stages with <b>no NeoForge native equivalent</b> are fired
 * only on Fabric (Fabric-primary). Cross-loader stages are fired on both.
 */
public final class PolyLevelRenderEvents
{
    // ── Tier 17-A: Frame events ───────────────────────────────────────────────

    /**
     * Fired at the very start of each rendered frame, before any level geometry
     * is submitted.
     * <p>
     * NeoForge: {@code RenderFrameEvent.Pre}<br>
     * Fabric: mixin on {@code GameRenderer#render} at HEAD
     */
    public static final PolyEvent<RenderFrameStart> RENDER_FRAME_START = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderFrameStart(mc)));

    /**
     * Fired at the very end of each rendered frame, after all geometry has been
     * submitted and presented.
     * <p>
     * NeoForge: {@code RenderFrameEvent.Post}<br>
     * Fabric: mixin on {@code GameRenderer#render} at RETURN
     */
    public static final PolyEvent<RenderFrameEnd> RENDER_FRAME_END = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderFrameEnd(mc)));

    // ── Tier 17-B: Level render stage events ─────────────────────────────────

    /**
     * Fired after the sky layer has been rendered.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterSky}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<RenderStage> AFTER_SKY = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired after opaque terrain features (plants, cross-shaped blocks, etc.)
     * have been rendered — one pass after {@code AFTER_OPAQUE_BLOCKS} in
     * {@link PolyRenderEvents}.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterOpaqueFeatures}<br>
     * Fabric: {@code LevelRenderEvents.AFTER_SOLID_FEATURES}
     */
    public static final PolyEvent<RenderStage> AFTER_OPAQUE_FEATURES = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired immediately before translucent terrain geometry is rendered.
     * <p>
     * NeoForge: no equivalent (Fabric-primary)<br>
     * Fabric: {@code LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN}
     */
    public static final PolyEvent<RenderStage> BEFORE_TRANSLUCENT_TERRAIN = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired after translucent terrain features have been rendered.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterTranslucentFeatures}<br>
     * Fabric: {@code LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES}
     */
    public static final PolyEvent<RenderStage> AFTER_TRANSLUCENT_FEATURES = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired after weather effects (rain, snow) have been rendered.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterWeather}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<RenderStage> AFTER_WEATHER = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired after the entire level (world) has been rendered for the current frame.
     * <p>
     * NeoForge: {@code RenderLevelStageEvent.AfterLevel}<br>
     * Fabric: {@code LevelRenderEvents.END_MAIN}
     */
    public static final PolyEvent<RenderStage> AFTER_LEVEL = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired at the start of the main level render pass.
     * <p>
     * NeoForge: no equivalent (Fabric-primary)<br>
     * Fabric: {@code LevelRenderEvents.START_MAIN}
     */
    public static final PolyEvent<RenderStage> START_MAIN = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired before the block selection outline is rendered.
     * Call {@link CancelContext#cancel()} to suppress the outline.
     * <p>
     * NeoForge: mixin on {@code LevelRenderer#extractBlockOutline} at HEAD<br>
     * Fabric: {@code LevelRenderEvents.BEFORE_BLOCK_OUTLINE} (returns {@code false} to cancel)
     */
    public static final PolyEvent<BeforeBlockOutline> BEFORE_BLOCK_OUTLINE = PolyEvent.create(
            handlers -> (mc, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onBeforeBlockOutline(mc, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired before gizmo overlays are rendered.
     * <p>
     * NeoForge: no equivalent (Fabric-primary)<br>
     * Fabric: {@code LevelRenderEvents.BEFORE_GIZMOS}
     */
    public static final PolyEvent<RenderStage> BEFORE_GIZMOS = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    /**
     * Fired during the submit-collection phase, after transparent geometry has
     * been dispatched but before the GPU frame is finalized.
     * <p>
     * NeoForge: no equivalent (Fabric-primary)<br>
     * Fabric: {@code LevelRenderEvents.COLLECT_SUBMITS}
     */
    public static final PolyEvent<RenderStage> COLLECT_SUBMITS = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onRenderStage(mc)));

    private PolyLevelRenderEvents() {}

    // ── SAM interfaces ────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface RenderFrameStart
    {
        void onRenderFrameStart(Minecraft client);
    }

    @FunctionalInterface
    public interface RenderFrameEnd
    {
        void onRenderFrameEnd(Minecraft client);
    }

    /** Common SAM for simple (non-cancellable) render stage hooks. */
    @FunctionalInterface
    public interface RenderStage
    {
        void onRenderStage(Minecraft client);
    }

    /** SAM for the cancellable {@link #BEFORE_BLOCK_OUTLINE} stage. */
    @FunctionalInterface
    public interface BeforeBlockOutline
    {
        void onBeforeBlockOutline(Minecraft client, CancelContext ctx);
    }
}
