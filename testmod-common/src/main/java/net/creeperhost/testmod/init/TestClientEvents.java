package net.creeperhost.testmod.init;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatRouter;
import net.creeperhost.polylib.chat.client.FloatingChatWindow;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.event.events.client.PolyCameraEvents;
import net.creeperhost.polylib.event.events.client.PolyClientBlockEntityEvents;
import net.creeperhost.polylib.event.events.client.PolyClientChunkEvents;
import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.creeperhost.polylib.event.events.client.PolyClientEntityEvents;
import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLifecycleEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLevelEvents;
import net.creeperhost.polylib.event.events.client.PolyClientPlayerEvents;
import net.creeperhost.polylib.event.events.client.PolyClientTickEvents;
import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.creeperhost.polylib.event.events.client.PolyLevelRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderStateEvents;
import net.creeperhost.polylib.event.events.client.PolyTooltipEvents;
import net.creeperhost.polylib.event.events.server.PolyRegistryEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static net.creeperhost.testmod.TestModCommon.LOGGER;

/**
 * Registers test handlers for all client-side PolyLib events.
 * Must be called from a client-only code path (e.g. {@link net.creeperhost.testmod.TestModClientCommon#init()}).
 */
public class TestClientEvents
{

    public static void init()
    {
        if(!TestEvents.testEvents) return;
        // =====================================================================
        // Client tick events
        // =====================================================================

        // Rate-limited to every 1200 client ticks to avoid log spam
        PolyClientTickEvents.CLIENT_TICK_START.register(mc ->
        {
            if (mc.level != null && mc.level.getGameTime() % 1200 == 0)
                LOGGER.info("[TestMod] CLIENT_TICK_START: gameTime={}", mc.level.getGameTime());
        });

        PolyClientTickEvents.CLIENT_TICK_END.register(mc ->
        {
            if (mc.level != null && mc.level.getGameTime() % 1200 == 0)
                LOGGER.info("[TestMod] CLIENT_TICK_END: gameTime={}", mc.level.getGameTime());
        });

        // Rate-limited to every 6000 ticks per level
        PolyClientTickEvents.CLIENT_LEVEL_TICK_END.register(level ->
        {
            if (level.getGameTime() % 6000 == 0)
                LOGGER.info("[TestMod] CLIENT_LEVEL_TICK_END: dim={}", level.dimension().identifier());
        });

        // =====================================================================
        // Client level (dimension) events
        // =====================================================================

        PolyClientLevelEvents.CLIENT_LEVEL_LOAD.register(level ->
                LOGGER.info("[TestMod] CLIENT_LEVEL_LOAD: {}", level.dimension().identifier()));

        PolyClientLevelEvents.CLIENT_LEVEL_UNLOAD.register(level ->
                LOGGER.info("[TestMod] CLIENT_LEVEL_UNLOAD: {}", level.dimension().identifier()));

        // =====================================================================
        // Client player events
        // =====================================================================

        PolyClientPlayerEvents.CLIENT_LOGIN.register(player ->
                LOGGER.info("[TestMod] CLIENT_LOGIN: local player joined ({})", player.getScoreboardName()));

        PolyClientPlayerEvents.LOGOUT.register(player ->
                LOGGER.info("[TestMod] CLIENT_LOGOUT: local player left ({})", player == null ? "null" : player.getScoreboardName()));

        // =====================================================================
        // Camera events
        // =====================================================================

        // FOV_MODIFIER: only log when FOV deviates from default (1.0) to avoid spam
        PolyCameraEvents.FOV_MODIFIER.register((player, fovHolder) ->
        {
            if (fovHolder[0] != 1.0f)
                LOGGER.debug("[TestMod] FOV_MODIFIER: player={} fov={}", player.getScoreboardName(), fovHolder[0]);
        });

        // CAMERA_SETUP: rate-limited — log once every ~1200 frames
        PolyCameraEvents.CAMERA_SETUP.register(ctx ->
        {
            // Very high-frequency; log only a 1-in-1200 sample using System.nanoTime
            if ((System.nanoTime() / 1_000_000L) % 60000 < 1)
                LOGGER.debug("[TestMod] CAMERA_SETUP: yaw={} pitch={} roll={}", ctx.getYaw(), ctx.getPitch(), ctx.getRoll());
        });

        // =====================================================================
        // Input events
        // =====================================================================

        // Key input: only log press (action==1) to avoid spam from holds/releases
        PolyInputEvents.INPUT_KEY.register((key, scanCode, action, modifiers) ->
        {
            if (action == 1) // 1 = press
                LOGGER.info("[TestMod] INPUT_KEY: key={} scan={} mods={}", key, scanCode, modifiers);
        });

        // Mouse button: only log press (action==1)
        PolyInputEvents.INPUT_MOUSE.register((button, action, modifiers) ->
        {
            if (action == 1) // 1 = press
                LOGGER.info("[TestMod] INPUT_MOUSE: button={} mods={}", button, modifiers);
        });

        // =====================================================================
        // Render events — logged only once on startup then silenced via a flag
        // to verify they fire without spamming the log every frame.
        // =====================================================================

        PolyRenderEvents.AFTER_OPAQUE_BLOCKS.register(poseStack ->
        {
            if (RenderEventFireFlags.afterOpaqueBlocks)
            {
                LOGGER.info("[TestMod] AFTER_OPAQUE_BLOCKS fired (first frame).");
                RenderEventFireFlags.afterOpaqueBlocks = false;
            }
        });

        PolyRenderEvents.AFTER_TRANSLUCENT_BLOCKS.register(poseStack ->
        {
            if (RenderEventFireFlags.afterTranslucentBlocks)
            {
                LOGGER.info("[TestMod] AFTER_TRANSLUCENT_BLOCKS fired (first frame).");
                RenderEventFireFlags.afterTranslucentBlocks = false;
            }
        });

        PolyRenderEvents.AFTER_TRANSLUCENT_PARTICLES.register(poseStack ->
        {
            if (RenderEventFireFlags.afterTranslucentParticles)
            {
                LOGGER.info("[TestMod] AFTER_TRANSLUCENT_PARTICLES fired (first frame).");
                RenderEventFireFlags.afterTranslucentParticles = false;
            }
        });

        PolyRenderEvents.RENDER_GUI_PRE.register((graphics, partialTick) ->
        {
            if (RenderEventFireFlags.renderGuiPre)
            {
                LOGGER.info("[TestMod] RENDER_GUI_PRE fired (first frame, partialTick={}).", partialTick);
                RenderEventFireFlags.renderGuiPre = false;
            }
        });

        PolyRenderEvents.RENDER_GUI_POST.register((graphics, partialTick) ->
        {
            if (RenderEventFireFlags.renderGuiPost)
            {
                LOGGER.info("[TestMod] RENDER_GUI_POST fired (first frame, partialTick={}).", partialTick);
                RenderEventFireFlags.renderGuiPost = false;
            }
        });

        // Per-layer overlay events — log first fire per layer id (one-shot via set)
        PolyRenderEvents.GUI_OVERLAY_PRE.register((layerId, graphics, partialTick, ctx) ->
        {
            if (RenderEventFireFlags.guiOverlayPreSeen.add(layerId.toString()))
                LOGGER.info("[TestMod] GUI_OVERLAY_PRE: layer={}", layerId);
        });

        PolyRenderEvents.GUI_OVERLAY_POST.register((layerId, graphics, partialTick) ->
        {
            if (RenderEventFireFlags.guiOverlayPostSeen.add(layerId.toString()))
                LOGGER.info("[TestMod] GUI_OVERLAY_POST: layer={}", layerId);
        });

        // ---- PolyTooltipEvents ----
        // Fires client-side when building item tooltips.
        PolyTooltipEvents.ITEM_TOOLTIP.register((stack, player, lines, flags) ->
        {
            if (RenderEventFireFlags.tooltipSeen.add(stack.getItem().toString()))
                LOGGER.info("[TestMod] ITEM_TOOLTIP: item={} lines={}", stack.getItem(), lines.size());
        });

        PolyTooltipEvents.ITEM_ATTRIBUTE_MODIFIERS.register((stack, modifiers) ->
        {
            if (!modifiers.isEmpty() && RenderEventFireFlags.attrModifierSeen.add(stack.getItem().toString()))
                LOGGER.info("[TestMod] ITEM_ATTRIBUTE_MODIFIERS: item={} modifiers={}", stack.getItem(), modifiers.size());
        });

        // ====================================================================
        // Client lifecycle events
        // ====================================================================

        PolyClientLifecycleEvents.CLIENT_STARTED.register(mc ->
                LOGGER.info("[TestMod] CLIENT_STARTED"));

        PolyClientLifecycleEvents.CLIENT_STOPPING.register(mc ->
                LOGGER.info("[TestMod] CLIENT_STOPPING"));

        // ====================================================================
        // Client level tick events (Tier 22)
        // ====================================================================

        PolyClientTickEvents.CLIENT_LEVEL_TICK_START.register(level ->
        {
            // Very high frequency — no-op
        });

        // ====================================================================
        // Registry events (client-side: dimension attributes)
        // ====================================================================

        PolyRegistryEvents.MODIFY_DIMENSION_ATTRIBUTES.register((dimType, builder, provider) ->
                LOGGER.info("[TestMod] MODIFY_DIMENSION_ATTRIBUTES: dimType={}", dimType));

        // ====================================================================
        // Client block-entity events
        // ====================================================================

        PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_LOAD.register((be, level) ->
        {
            // Very high frequency — no-op
        });

        PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_UNLOAD.register((be, level) ->
        {
            // Very high frequency — no-op
        });

        // ====================================================================
        // Client chunk events
        // ====================================================================

        PolyClientChunkEvents.CLIENT_CHUNK_LOAD.register((level, chunk) ->
        {
            // Very high frequency — no-op
        });

        PolyClientChunkEvents.CLIENT_CHUNK_UNLOAD.register((level, chunk) ->
        {
            // Very high frequency — no-op
        });

        // ====================================================================
        // Client entity events
        // ====================================================================

        PolyClientEntityEvents.CLIENT_ENTITY_LOAD.register((entity, level) ->
        {
            // Very high frequency — no-op
        });

        PolyClientEntityEvents.CLIENT_ENTITY_UNLOAD.register((entity, level) ->
        {
            // Very high frequency — no-op
        });

        // ====================================================================
        // Client interaction events
        // ====================================================================

        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_BEFORE.register((player, level, pos, state, ctx) ->
                LOGGER.info("[TestMod] CLIENT_BLOCK_BREAK_BEFORE: {} at {}", state.getBlock().getDescriptionId(), pos));

        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_AFTER.register((player, level, pos, state) ->
                LOGGER.info("[TestMod] CLIENT_BLOCK_BREAK_AFTER: {} at {}", state.getBlock().getDescriptionId(), pos));

        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_CANCELED.register((player, level, pos, state) ->
                LOGGER.info("[TestMod] CLIENT_BLOCK_BREAK_CANCELED: {} at {}", state.getBlock().getDescriptionId(), pos));

        PolyClientInteractionEvents.CLIENT_PRE_ATTACK.register((client, player, clickCount, ctx) ->
        {
            // Very high frequency — no-op
        });

        // ====================================================================
        // Client connection events
        // ====================================================================

        PolyClientConnectionEvents.CLIENT_PLAY_INIT.register((handler, sender, client) ->
                LOGGER.info("[TestMod] CLIENT_PLAY_INIT"));

        PolyClientConnectionEvents.CLIENT_PLAY_JOIN.register((handler, sender, client) ->
                LOGGER.info("[TestMod] CLIENT_PLAY_JOIN"));

        PolyClientConnectionEvents.CLIENT_PLAY_DISCONNECT.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_PLAY_DISCONNECT"));

        PolyClientConnectionEvents.CLIENT_LOGIN_INIT.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_LOGIN_INIT"));

        PolyClientConnectionEvents.CLIENT_LOGIN_QUERY_START.register((handler, client, sender, payload) ->
                LOGGER.info("[TestMod] CLIENT_LOGIN_QUERY_START: payload={}", payload.id()));

        PolyClientConnectionEvents.CLIENT_LOGIN_QUERY_RESPONSE.register((handler, client, payload) ->
                LOGGER.info("[TestMod] CLIENT_LOGIN_QUERY_RESPONSE: payload={}", payload.id()));

        PolyClientConnectionEvents.CLIENT_LOGIN_DISCONNECT.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_LOGIN_DISCONNECT"));

        PolyClientConnectionEvents.CLIENT_CONFIGURATION_INIT.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_CONFIGURATION_INIT"));

        PolyClientConnectionEvents.CLIENT_CONFIGURATION_COMPLETE.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_CONFIGURATION_COMPLETE"));

        PolyClientConnectionEvents.CLIENT_CONFIGURATION_DISCONNECT.register((handler, client) ->
                LOGGER.info("[TestMod] CLIENT_CONFIGURATION_DISCONNECT"));

        // ====================================================================
        // Level render stage events (PolyLevelRenderEvents)
        // ====================================================================

        // Frame boundary events — log only on first fire
        PolyLevelRenderEvents.RENDER_FRAME_START.register(mc ->
        {
            if (RenderEventFireFlags.renderFrameStart)
            {
                LOGGER.info("[TestMod] RENDER_FRAME_START fired (first frame).");
                RenderEventFireFlags.renderFrameStart = false;
            }
        });

        PolyLevelRenderEvents.RENDER_FRAME_END.register(mc ->
        {
            if (RenderEventFireFlags.renderFrameEnd)
            {
                LOGGER.info("[TestMod] RENDER_FRAME_END fired (first frame).");
                RenderEventFireFlags.renderFrameEnd = false;
            }
        });

        // Level render stage events — NeoForge-primary (may not fire on Fabric)
        PolyLevelRenderEvents.AFTER_SKY.register(mc ->
        {
            if (RenderEventFireFlags.afterSky)
            {
                LOGGER.info("[TestMod] AFTER_SKY fired (first frame, NeoForge-primary).");
                RenderEventFireFlags.afterSky = false;
            }
        });

        PolyLevelRenderEvents.AFTER_OPAQUE_FEATURES.register(mc ->
        {
            if (RenderEventFireFlags.afterOpaqueFeatures)
            {
                LOGGER.info("[TestMod] AFTER_OPAQUE_FEATURES fired (first frame).");
                RenderEventFireFlags.afterOpaqueFeatures = false;
            }
        });

        // Fabric-primary — will not fire on NeoForge
        PolyLevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(mc ->
        {
            if (RenderEventFireFlags.beforeTranslucentTerrain)
            {
                LOGGER.info("[TestMod] BEFORE_TRANSLUCENT_TERRAIN fired (first frame, Fabric-primary).");
                RenderEventFireFlags.beforeTranslucentTerrain = false;
            }
        });

        PolyLevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(mc ->
        {
            if (RenderEventFireFlags.afterTranslucentFeatures)
            {
                LOGGER.info("[TestMod] AFTER_TRANSLUCENT_FEATURES fired (first frame).");
                RenderEventFireFlags.afterTranslucentFeatures = false;
            }
        });

        PolyLevelRenderEvents.AFTER_WEATHER.register(mc ->
        {
            if (RenderEventFireFlags.afterWeather)
            {
                LOGGER.info("[TestMod] AFTER_WEATHER fired (first frame, NeoForge-primary).");
                RenderEventFireFlags.afterWeather = false;
            }
        });

        PolyLevelRenderEvents.AFTER_LEVEL.register(mc ->
        {
            if (RenderEventFireFlags.afterLevel)
            {
                LOGGER.info("[TestMod] AFTER_LEVEL fired (first frame).");
                RenderEventFireFlags.afterLevel = false;
            }
        });

        // Fabric-primary — will not fire on NeoForge
        PolyLevelRenderEvents.START_MAIN.register(mc ->
        {
            if (RenderEventFireFlags.startMain)
            {
                LOGGER.info("[TestMod] START_MAIN fired (first frame, Fabric-primary).");
                RenderEventFireFlags.startMain = false;
            }
        });

        // Cancellable — do not cancel in the test handler
        PolyLevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((mc, ctx) ->
        {
            if (RenderEventFireFlags.beforeBlockOutline)
            {
                LOGGER.info("[TestMod] BEFORE_BLOCK_OUTLINE fired (first time).");
                RenderEventFireFlags.beforeBlockOutline = false;
            }
        });

        // Fabric-primary — will not fire on NeoForge
        PolyLevelRenderEvents.BEFORE_GIZMOS.register(mc ->
        {
            if (RenderEventFireFlags.beforeGizmos)
            {
                LOGGER.info("[TestMod] BEFORE_GIZMOS fired (first frame, Fabric-primary).");
                RenderEventFireFlags.beforeGizmos = false;
            }
        });

        PolyLevelRenderEvents.COLLECT_SUBMITS.register(mc ->
        {
            if (RenderEventFireFlags.collectSubmits)
            {
                LOGGER.info("[TestMod] COLLECT_SUBMITS fired (first frame, Fabric-primary).");
                RenderEventFireFlags.collectSubmits = false;
            }
        });

        // ====================================================================
        // Render state invalidation events (PolyRenderStateEvents)
        // ====================================================================

        PolyRenderStateEvents.INVALIDATE_RENDER_STATE.register(() ->
                LOGGER.info("[TestMod] INVALIDATE_RENDER_STATE fired (resource reload / settings change)."));
    }

    /**
     * One-shot fire flags for render events.
     * Reset to {@code true} on re-init or for re-testing by setting them back to true in-game.
     */
    public static final class RenderEventFireFlags
    {
        public static boolean afterOpaqueBlocks = true;
        public static boolean afterTranslucentBlocks = true;
        public static boolean afterTranslucentParticles = true;
        public static boolean renderGuiPre = true;
        public static boolean renderGuiPost = true;
        /** Tracks which overlay layer ids have been seen — logs each id only once. */
        public static final Set<String> guiOverlayPreSeen = Collections.synchronizedSet(new HashSet<>());
        public static final Set<String> guiOverlayPostSeen = Collections.synchronizedSet(new HashSet<>());
        /** Tracks items whose tooltip has already been logged — logs each item only once. */
        public static final Set<String> tooltipSeen = Collections.synchronizedSet(new HashSet<>());
        /** Tracks items whose attribute modifiers tooltip has been logged — logs each item only once. */
        public static final Set<String> attrModifierSeen = Collections.synchronizedSet(new HashSet<>());

        // ---- PolyLevelRenderEvents fire flags ----
        public static boolean renderFrameStart = true;
        public static boolean renderFrameEnd = true;
        public static boolean afterSky = true;
        public static boolean afterOpaqueFeatures = true;
        public static boolean beforeTranslucentTerrain = true;
        public static boolean afterTranslucentFeatures = true;
        public static boolean afterWeather = true;
        public static boolean afterLevel = true;
        public static boolean startMain = true;
        public static boolean beforeBlockOutline = true;
        public static boolean beforeGizmos = true;
        public static boolean collectSubmits = true;

        private RenderEventFireFlags() {}
    }
}
