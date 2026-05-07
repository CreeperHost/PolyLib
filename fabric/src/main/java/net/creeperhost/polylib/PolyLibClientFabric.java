package net.creeperhost.polylib;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyClientBlockEntityEvents;
import net.creeperhost.polylib.event.events.client.PolyClientChunkEvents;
import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.creeperhost.polylib.event.events.client.PolyClientEntityEvents;
import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLevelEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLifecycleEvents;
import net.creeperhost.polylib.event.events.client.PolyClientPlayerEvents;
import net.creeperhost.polylib.event.events.client.PolyClientTickEvents;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyLevelRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderStateEvents;
import net.creeperhost.polylib.event.events.client.PolyScreenEvents;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient;
import net.creeperhost.polylib.chunkmap.common.network.*;
import net.creeperhost.polylib.client.screen.chunkmap.PolyChunkMapKeys;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class PolyLibClientFabric
{
    public static void init()
    {
        FabricEventsClient.init();
        PolyLibNetwork.initClient();

        // ── T19: Screen Lifecycle, Tick, Input (Fabric) ────────────────────────
        ScreenEvents.BEFORE_INIT.register((mc, screen, w, h) -> {
            CancelContext ctx = new CancelContext();
            PolyScreenEvents.SCREEN_OPENING.invoker().onScreenOpening(screen, null, ctx);
            // Fabric BEFORE_INIT isn't natively cancellable to stop the screen from opening,
            // but we fire the event anyway.
        });

        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            PolyScreenEvents.SCREEN_OPENED.invoker().onScreenOpened(mc, screen, w, h);
            
            ScreenEvents.remove(screen).register(s -> PolyScreenEvents.SCREEN_CLOSING.invoker().onScreenClosing(s));
            
            ScreenEvents.beforeTick(screen).register(s -> PolyScreenEvents.SCREEN_TICK_BEFORE.invoker().onScreenTickBefore(s));
            ScreenEvents.afterTick(screen).register(s -> PolyScreenEvents.SCREEN_TICK_AFTER.invoker().onScreenTickAfter(s));

            // Keyboard
            ScreenKeyboardEvents.allowKeyPress(screen).register((s, ke) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_KEY_PRESS_ALLOW.invoker()
                        .onScreenKeyPressAllow(s, ke.key(), ke.scancode(), ke.modifiers(), ctx);
                return !ctx.isCancelled();
            });
            ScreenKeyboardEvents.beforeKeyPress(screen).register((s, ke) ->
                    PolyScreenEvents.SCREEN_KEY_PRESS_BEFORE.invoker().onScreenKeyPressBefore(s, ke.key(), ke.scancode(), ke.modifiers()));
            ScreenKeyboardEvents.afterKeyPress(screen).register((s, ke) ->
                    PolyScreenEvents.SCREEN_KEY_PRESS_AFTER.invoker().onScreenKeyPressAfter(s, ke.key(), ke.scancode(), ke.modifiers()));

            ScreenKeyboardEvents.allowKeyRelease(screen).register((s, ke) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_KEY_RELEASE_ALLOW.invoker()
                        .onScreenKeyReleaseAllow(s, ke.key(), ke.scancode(), ke.modifiers(), ctx);
                return !ctx.isCancelled();
            });
            ScreenKeyboardEvents.beforeKeyRelease(screen).register((s, ke) ->
                    PolyScreenEvents.SCREEN_KEY_RELEASE_BEFORE.invoker().onScreenKeyReleaseBefore(s, ke.key(), ke.scancode(), ke.modifiers()));
            ScreenKeyboardEvents.afterKeyRelease(screen).register((s, ke) ->
                    PolyScreenEvents.SCREEN_KEY_RELEASE_AFTER.invoker().onScreenKeyReleaseAfter(s, ke.key(), ke.scancode(), ke.modifiers()));

            // Mouse
            ScreenMouseEvents.allowMouseClick(screen).register((s, mbe) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_MOUSE_CLICK_ALLOW.invoker()
                        .onScreenMouseClickAllow(s, mbe.x(), mbe.y(), mbe.button(), ctx);
                return !ctx.isCancelled();
            });
            ScreenMouseEvents.beforeMouseClick(screen).register((s, mbe) ->
                    PolyScreenEvents.SCREEN_MOUSE_CLICK_BEFORE.invoker().onScreenMouseClickBefore(s, mbe.x(), mbe.y(), mbe.button()));
            ScreenMouseEvents.afterMouseClick(screen).register((s, mbe, handled) -> {
                PolyScreenEvents.SCREEN_MOUSE_CLICK_AFTER.invoker().onScreenMouseClickAfter(s, mbe.x(), mbe.y(), mbe.button());
                return handled;
            });

            ScreenMouseEvents.allowMouseRelease(screen).register((s, mbe) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_MOUSE_RELEASE_ALLOW.invoker()
                        .onScreenMouseReleaseAllow(s, mbe.x(), mbe.y(), mbe.button(), ctx);
                return !ctx.isCancelled();
            });
            ScreenMouseEvents.beforeMouseRelease(screen).register((s, mbe) ->
                    PolyScreenEvents.SCREEN_MOUSE_RELEASE_BEFORE.invoker().onScreenMouseReleaseBefore(s, mbe.x(), mbe.y(), mbe.button()));
            ScreenMouseEvents.afterMouseRelease(screen).register((s, mbe, handled) -> {
                PolyScreenEvents.SCREEN_MOUSE_RELEASE_AFTER.invoker().onScreenMouseReleaseAfter(s, mbe.x(), mbe.y(), mbe.button());
                return handled;
            });

            ScreenMouseEvents.allowMouseScroll(screen).register((s, mx, my, sx, sy) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_MOUSE_SCROLL_ALLOW.invoker()
                        .onScreenMouseScrollAllow(s, mx, my, sx, sy, ctx);
                return !ctx.isCancelled();
            });
            ScreenMouseEvents.beforeMouseScroll(screen).register((s, mx, my, sx, sy) ->
                    PolyScreenEvents.SCREEN_MOUSE_SCROLL_BEFORE.invoker().onScreenMouseScrollBefore(s, mx, my, sx, sy));
            ScreenMouseEvents.afterMouseScroll(screen).register((s, mx, my, sx, sy, handled) -> {
                PolyScreenEvents.SCREEN_MOUSE_SCROLL_AFTER.invoker().onScreenMouseScrollAfter(s, mx, my, sx, sy);
                return handled;
            });

            ScreenMouseEvents.allowMouseDrag(screen).register((s, mbe, dx, dy) -> {
                CancelContext ctx = new CancelContext();
                PolyScreenEvents.SCREEN_MOUSE_DRAG_ALLOW.invoker()
                        .onScreenMouseDragAllow(s, mbe.x(), mbe.y(), mbe.button(), dx, dy, ctx);
                return !ctx.isCancelled();
            });
            ScreenMouseEvents.beforeMouseDrag(screen).register((s, mbe, dx, dy) ->
                    PolyScreenEvents.SCREEN_MOUSE_DRAG_BEFORE.invoker().onScreenMouseDragBefore(s, mbe.x(), mbe.y(), mbe.button(), dx, dy));
            ScreenMouseEvents.afterMouseDrag(screen).register((s, mbe, dx, dy, handled) -> {
                PolyScreenEvents.SCREEN_MOUSE_DRAG_AFTER.invoker().onScreenMouseDragAfter(s, mbe.x(), mbe.y(), mbe.button(), dx, dy);
                return handled;
            });
        });

        // PolyLib client tick events
        ClientTickEvents.START_CLIENT_TICK.register(mc -> PolyClientTickEvents.CLIENT_TICK_START.invoker().onTickStart(mc));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> PolyClientTickEvents.CLIENT_TICK_END.invoker().onTickEnd(mc));
        ClientTickEvents.END_LEVEL_TICK.register(level -> PolyClientTickEvents.CLIENT_LEVEL_TICK_END.invoker().onLevelTickEnd(level));
        ClientTickEvents.START_LEVEL_TICK.register(level -> PolyClientTickEvents.CLIENT_LEVEL_TICK_START.invoker().onTickStart(level));

        // PolyLib client level events
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((mc, level) ->
        {
            if (level != null)
            {
                PolyClientLevelEvents.CLIENT_LEVEL_LOAD.invoker().onLoad(level);
            }
            // Unload: level == null means the level was unloaded but we can't pass it — omit
        });

        // PolyLib client player events
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            if (client.player != null)
            {
                PolyClientPlayerEvents.CLIENT_LOGIN.invoker().onLogin(client.player);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            if (client.player != null)
            {
                PolyClientPlayerEvents.LOGOUT.invoker().onLogout(client.player);
            }
        });

        // PolyLib render events
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(ctx ->
                PolyRenderEvents.AFTER_TRANSLUCENT_PARTICLES.invoker().onRender(ctx.poseStack()));

        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(ctx ->
                PolyRenderEvents.AFTER_TRANSLUCENT_BLOCKS.invoker().onRender(ctx.poseStack()));

        LevelRenderEvents.AFTER_OPAQUE_TERRAIN.register(ctx ->
        {
            if (ctx instanceof LevelRenderContext renderCtx)
            {
                PolyRenderEvents.AFTER_OPAQUE_BLOCKS.invoker().onRender(renderCtx.poseStack());
            }
        });

        // ── T16: Client lifecycle ──────────────────────────────────────────────
        ClientLifecycleEvents.CLIENT_STARTED.register(mc ->
                PolyClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(mc));
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc ->
                PolyClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(mc));

        // ── T16: Client chunk events ───────────────────────────────────────────
        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) ->
                PolyClientChunkEvents.CLIENT_CHUNK_LOAD.invoker().onChunkLoad(level, chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
                PolyClientChunkEvents.CLIENT_CHUNK_UNLOAD.invoker().onChunkUnload(level, chunk));

        // ── T16: Client entity events ──────────────────────────────────────────
        ClientEntityEvents.ENTITY_LOAD.register((entity, level) ->
                PolyClientEntityEvents.CLIENT_ENTITY_LOAD.invoker().onEntityLoad(entity, level));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
                PolyClientEntityEvents.CLIENT_ENTITY_UNLOAD.invoker().onEntityUnload(entity, level));

        // ── T16: Client block entity events ───────────────────────────────────
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, level) ->
                PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_LOAD.invoker().onBlockEntityLoad(be, level));
        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, level) ->
                PolyClientBlockEntityEvents.CLIENT_BLOCK_ENTITY_UNLOAD.invoker().onBlockEntityUnload(be, level));

        // ── T16: Client connection events ─────────────────────────────────────
        // Play init (INIT SAM: (ClientPacketListener, Minecraft) — no PacketSender in this Fabric API version)
        ClientPlayConnectionEvents.INIT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_PLAY_INIT.invoker().onPlayInit(handler, null, client));
        // Play join and disconnect — fire PolyClientConnectionEvents IN ADDITION to PolyClientPlayerEvents
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                PolyClientConnectionEvents.CLIENT_PLAY_JOIN.invoker().onPlayJoin(handler, sender, client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_PLAY_DISCONNECT.invoker().onPlayDisconnect(handler, client));

        // Login
        ClientLoginConnectionEvents.INIT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_LOGIN_INIT.invoker().onLoginInit(handler, client));
        // QUERY_START SAM: (ClientHandshakePacketListenerImpl, Minecraft) — no PacketSender/payload in this Fabric API version
        ClientLoginConnectionEvents.QUERY_START.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_LOGIN_QUERY_START.invoker().onQueryStart(handler, client, null, null));
        ClientLoginConnectionEvents.DISCONNECT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_LOGIN_DISCONNECT.invoker().onLoginDisconnect(handler, client));

        // Configuration
        ClientConfigurationConnectionEvents.INIT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_CONFIGURATION_INIT.invoker().onConfigurationInit(handler, client));
        ClientConfigurationConnectionEvents.COMPLETE.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_CONFIGURATION_COMPLETE.invoker().onConfigurationComplete(handler, client));
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) ->
                PolyClientConnectionEvents.CLIENT_CONFIGURATION_DISCONNECT.invoker().onConfigurationDisconnect(handler, client));

        // ── T16: Client interaction events ────────────────────────────────────
        // BEFORE — no native Fabric event; handled by FabricClientBlockBreakBeforeMixin
        // AFTER — native Fabric
        ClientPlayerBlockBreakEvents.AFTER.register((level, player, pos, state) ->
                PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_AFTER.invoker()
                        .onBlockBreakAfter((net.minecraft.client.player.LocalPlayer) player,
                                (ClientLevel) level, pos, state));
        // CANCELED — no native Fabric event; handled by FabricClientBlockBreakCanceledMixin
        // PRE_ATTACK — native Fabric
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) ->
        {
            CancelContext ctx = new CancelContext();
            PolyClientInteractionEvents.CLIENT_PRE_ATTACK.invoker()
                    .onPreAttack(client, player, clickCount, ctx);
            return ctx.isCancelled();
        });

        // ── T17-A: Frame events ───────────────────────────────────────────────────
        // RENDER_FRAME_START / RENDER_FRAME_END are wired via Fabric mixins on GameRenderer#render

        // ── T17-B: Level render stage events (Fabric-side) ────────────────────
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx ->
                PolyLevelRenderEvents.AFTER_OPAQUE_FEATURES.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(ctx ->
                PolyLevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(ctx ->
                PolyLevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.END_MAIN.register(ctx ->
                PolyLevelRenderEvents.AFTER_LEVEL.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.START_MAIN.register(ctx ->
                PolyLevelRenderEvents.START_MAIN.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hitResult) ->
        {
            CancelContext bboCtx = new CancelContext();
            PolyLevelRenderEvents.BEFORE_BLOCK_OUTLINE.invoker()
                    .onBeforeBlockOutline(Minecraft.getInstance(), bboCtx);
            return !bboCtx.isCancelled();
        });

        LevelRenderEvents.BEFORE_GIZMOS.register(ctx ->
                PolyLevelRenderEvents.BEFORE_GIZMOS.invoker().onRenderStage(Minecraft.getInstance()));

        LevelRenderEvents.COLLECT_SUBMITS.register(ctx ->
                PolyLevelRenderEvents.COLLECT_SUBMITS.invoker().onRenderStage(Minecraft.getInstance()));

        // ── T17-C: Client level changed ──────────────────────────────────────────
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((mc, level) ->
                PolyClientLifecycleEvents.CLIENT_LEVEL_CHANGED.invoker().onClientLevelChanged(mc, level));

        // ── T17-D: Invalidate render state ─────────────────────────────────────
        InvalidateRenderStateCallback.EVENT.register(() ->
                PolyRenderStateEvents.INVALIDATE_RENDER_STATE.invoker().onInvalidate());

        // ── T18-E: Cape render gate ────────────────────────────────────────────
        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(renderState ->
        {
            CancelContext capeCtx = new CancelContext();
            PolyEntityRenderEvents.ALLOW_CAPE_RENDER.invoker().onAllowCapeRender(renderState, capeCtx);
            return !capeCtx.isCancelled();
        });
    }
}
