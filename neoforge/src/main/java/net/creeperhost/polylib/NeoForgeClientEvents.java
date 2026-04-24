package net.creeperhost.polylib;

import net.creeperhost.polylib.client.config.ConfigPanelRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyCameraEvents;
import net.creeperhost.polylib.event.events.client.PolyClientConnectionEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLevelEvents;
import net.creeperhost.polylib.event.events.client.PolyClientLifecycleEvents;
import net.creeperhost.polylib.event.events.client.PolyClientPlayerEvents;
import net.creeperhost.polylib.event.events.client.PolyClientTickEvents;
import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.creeperhost.polylib.event.events.client.PolyLevelRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderEvents;
import net.creeperhost.polylib.event.events.client.PolyRenderStateEvents;
import net.creeperhost.polylib.event.events.client.PolyScreenEvents;
import net.creeperhost.polylib.event.events.client.PolyTooltipEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventInitScreenEvent(ScreenEvent.Init.Post event)
    {
        ModularGuiInjector.initPost(event.getScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientTickEvent.Post event)
    {
        ModularGuiInjector.tick(Minecraft.getInstance());
        ConfigPanelRegistry.tickKeybinds();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventGuiRenderPost(ScreenEvent.Render.Post event)
    {
        ModularGuiInjector.renderPost(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onKeyPressed(ScreenEvent.KeyPressed.Post event)
    {
        ModularGuiInjector.keyPressed(event.getScreen().getMinecraft(), event.getScreen(), event.getKeyEvent());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onKeyReleased(ScreenEvent.KeyReleased.Post event)
    {
        ModularGuiInjector.keyReleased(event.getScreen().getMinecraft(), event.getScreen(), event.getKeyEvent());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCharTyped(ScreenEvent.CharacterTyped.Post event)
    {
        ModularGuiInjector.charTyped(event.getScreen().getMinecraft(), event.getScreen(), event.getCharacterEvent());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseScrolled(ScreenEvent.MouseScrolled.Post event)
    {
        ModularGuiInjector.mouseScrolled(event.getScreen().getMinecraft(), event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseClicked(ScreenEvent.MouseButtonPressed.Post event)
    {
        ModularGuiInjector.mouseClicked(event.getScreen().getMinecraft(), event.getScreen(), event.getMouseButtonEvent(), true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void mouseReleased(ScreenEvent.MouseButtonReleased.Post event)
    {
        ModularGuiInjector.mouseReleased(event.getScreen().getMinecraft(), event.getScreen(), event.getMouseButtonEvent());
    }

    // -------------------------------------------------------------------------
    // PolyLib client events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPolyClientTickEnd(ClientTickEvent.Post event)
    {
        PolyClientTickEvents.CLIENT_TICK_END.invoker().onTickEnd(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event)
    {
        if (event.getPlayer() != null)
        {
            PolyClientPlayerEvents.LOGOUT.invoker().onLogout(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onRenderLevelAfterTranslucentParticles(RenderLevelStageEvent.AfterTranslucentParticles event)
    {
        PolyRenderEvents.AFTER_TRANSLUCENT_PARTICLES.invoker().onRender(event.getPoseStack());
    }

    @SubscribeEvent
    public static void onRenderLevelAfterTranslucentBlocks(RenderLevelStageEvent.AfterTranslucentBlocks event)
    {
        PolyRenderEvents.AFTER_TRANSLUCENT_BLOCKS.invoker().onRender(event.getPoseStack());
    }

    // -------------------------------------------------------------------------
    // Tier 3: Client events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPolyClientTickStart(ClientTickEvent.Pre event)
    {
        PolyClientTickEvents.CLIENT_TICK_START.invoker().onTickStart(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onClientLevelTickEnd(LevelTickEvent.Post event)
    {
        if (event.getLevel() instanceof ClientLevel cl)
        {
            PolyClientTickEvents.CLIENT_LEVEL_TICK_END.invoker().onLevelTickEnd(cl);
        }
    }

    @SubscribeEvent
    public static void onClientLevelLoad(LevelEvent.Load event)
    {
        if (event.getLevel() instanceof ClientLevel cl)
        {
            PolyClientLevelEvents.CLIENT_LEVEL_LOAD.invoker().onLoad(cl);
        }
    }

    @SubscribeEvent
    public static void onClientLevelUnload(LevelEvent.Unload event)
    {
        if (event.getLevel() instanceof ClientLevel cl)
        {
            PolyClientLevelEvents.CLIENT_LEVEL_UNLOAD.invoker().onUnload(cl);
        }
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event)
    {
        if (event.getPlayer() != null)
        {
            PolyClientPlayerEvents.CLIENT_LOGIN.invoker().onLogin(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onRenderLevelAfterOpaqueBlocks(RenderLevelStageEvent.AfterOpaqueBlocks event)
    {
        PolyRenderEvents.AFTER_OPAQUE_BLOCKS.invoker().onRender(event.getPoseStack());
    }

    @SubscribeEvent
    public static void onRenderGuiPre(net.neoforged.neoforge.client.event.RenderGuiEvent.Pre event)
    {
        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        CancelContext ctx = new CancelContext();
        PolyGuiEvents.RENDER_GUI_PRE.invoker().onRenderGuiPre(
                event.getGuiGraphics(), pt, ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderGuiPost(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event)
    {
        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        PolyGuiEvents.RENDER_GUI_POST.invoker().onRenderGuiPost(
                event.getGuiGraphics(), pt);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event)
    {
        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        CancelContext ctx = new CancelContext();
        PolyGuiEvents.RENDER_HUD_LAYER_PRE.invoker().onRenderHudLayerPre(
                event.getGuiGraphics(), pt, event.getName(), ctx);
        PolyRenderEvents.GUI_OVERLAY_PRE.invoker().onGuiOverlayPre(event.getName(), event.getGuiGraphics(), pt, ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPost(RenderGuiLayerEvent.Post event)
    {
        float pt = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        PolyGuiEvents.RENDER_HUD_LAYER_POST.invoker().onRenderHudLayerPost(
                event.getGuiGraphics(), pt, event.getName());
        PolyRenderEvents.GUI_OVERLAY_POST.invoker().onGuiOverlayPost(event.getName(), event.getGuiGraphics(), pt);
    }

    // TODO: CUSTOMIZE_HUD_OVERLAY — CustomizeGuiOverlayEvent is abstract; subclasses must be used.
    // The common interface expects a String overlay-type identifier, but NeoForge only exposes Window here.
    // Re-enable (against a specific subclass) when a mapping is defined.
    // @SubscribeEvent
    // public static void onCustomizeGuiOverlay(CustomizeGuiOverlayEvent event) { ... }

    @SubscribeEvent
    public static void onScreenshot(net.neoforged.neoforge.client.event.ScreenshotEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyGuiEvents.SCREENSHOT.invoker().onScreenshot(
                event.getScreenshotFile(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onToastAdd(net.neoforged.neoforge.client.event.ToastAddEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyGuiEvents.TOAST_ADD.invoker().onToastAdd(event.getToast(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // TODO: PlayerHeartTypeEvent does not exist in NeoForge 26.1.2.22-beta.
    // Re-enable and connect to PolyGuiEvents.PLAYER_HEART_TYPE when the event is available.
    // @SubscribeEvent
    // public static void onPlayerHeartType(net.neoforged.neoforge.client.event.PlayerHeartTypeEvent event) { ... }

    // TODO: ContainerScreenEvent.Render.Background does not exist in NeoForge 26.1.2.22-beta (only Foreground exists).
    // @SubscribeEvent
    // public static void onContainerScreenBackground(ContainerScreenEvent.Render.Background event) { ... }

    @SubscribeEvent
    public static void onContainerScreenForeground(net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Foreground event)
    {
        PolyGuiEvents.CONTAINER_SCREEN_RENDER_FG.invoker().onContainerScreenRenderFg(
                event.getContainerScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
    }

    @SubscribeEvent
    public static void onInputKey(InputEvent.Key event)
    {
        PolyInputEvents.INPUT_KEY.invoker().onKeyInput(
                event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onInputMouse(InputEvent.MouseButton.Post event)
    {
        PolyInputEvents.INPUT_MOUSE.invoker().onMouseInput(
                event.getButton(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event)
    {
        if (Minecraft.getInstance().player != null)
        {
            float[] fovHolder = { event.getNewFovModifier() };
            PolyCameraEvents.FOV_MODIFIER.invoker().onComputeFov(Minecraft.getInstance().player, fovHolder);
            event.setNewFovModifier(fovHolder[0]);
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event)
    {
        PolyCameraEvents.CameraContext ctx = new PolyCameraEvents.CameraContext(
                event.getYaw(), event.getPitch(), event.getRoll());
        PolyCameraEvents.CAMERA_SETUP.invoker().onCameraSetup(ctx);
        event.setYaw(ctx.getYaw());
        event.setPitch(ctx.getPitch());
        event.setRoll(ctx.getRoll());
    }

    // ── T10: Tooltip ──────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        PolyTooltipEvents.ITEM_TOOLTIP.invoker().onItemTooltip(
                event.getItemStack(), event.getEntity(), event.getToolTip(), event.getFlags());
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event)
    {
        PolyTooltipEvents.ITEM_ATTRIBUTE_MODIFIERS.invoker().onItemAttributeModifiers(
                event.getItemStack(), event.getModifiers());
    }

    // ── T16: Client lifecycle ─────────────────────────────────────────────────

    @SubscribeEvent
    public static void onClientStopping(GameShuttingDownEvent event)
    {
        PolyClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(Minecraft.getInstance());
    }

    // ── T16: Client level tick start ──────────────────────────────────────────

    @SubscribeEvent
    public static void onClientLevelTickStart(LevelTickEvent.Pre event)
    {
        if (event.getLevel() instanceof ClientLevel cl)
        {
            PolyClientTickEvents.CLIENT_LEVEL_TICK_START.invoker().onTickStart(cl);
        }
    }

    // ── T16: Client connection events (play channel — native NeoForge) ────────

    @SubscribeEvent
    public static void onClientPlayJoin(ClientPlayerNetworkEvent.LoggingIn event)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener handler = mc.getConnection();
        if (handler != null)
        {
            PolyClientConnectionEvents.CLIENT_PLAY_JOIN.invoker().onPlayJoin(handler, null, mc);
        }
    }

    @SubscribeEvent
    public static void onClientPlayDisconnectFull(ClientPlayerNetworkEvent.LoggingOut event)
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener handler = mc.getConnection();
        if (handler != null)
        {
            PolyClientConnectionEvents.CLIENT_PLAY_DISCONNECT.invoker().onPlayDisconnect(handler, mc);
        }
    }

    // ── T17-A: Frame events ───────────────────────────────────────────────────

    @SubscribeEvent
    public static void onRenderFrameStart(RenderFrameEvent.Pre event)
    {
        PolyLevelRenderEvents.RENDER_FRAME_START.invoker().onRenderFrameStart(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderFrameEnd(RenderFrameEvent.Post event)
    {
        PolyLevelRenderEvents.RENDER_FRAME_END.invoker().onRenderFrameEnd(Minecraft.getInstance());
    }

    // ── T17-B: Level render stage events ─────────────────────────────────────

    @SubscribeEvent
    public static void onRenderLevelAfterSky(RenderLevelStageEvent.AfterSky event)
    {
        PolyLevelRenderEvents.AFTER_SKY.invoker().onRenderStage(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelAfterOpaqueFeatures(RenderLevelStageEvent.AfterOpaqueFeatures event)
    {
        PolyLevelRenderEvents.AFTER_OPAQUE_FEATURES.invoker().onRenderStage(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelAfterTranslucentFeatures(RenderLevelStageEvent.AfterTranslucentFeatures event)
    {
        PolyLevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.invoker().onRenderStage(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelAfterWeather(RenderLevelStageEvent.AfterWeather event)
    {
        PolyLevelRenderEvents.AFTER_WEATHER.invoker().onRenderStage(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderLevelAfterLevel(RenderLevelStageEvent.AfterLevel event)
    {
        PolyLevelRenderEvents.AFTER_LEVEL.invoker().onRenderStage(Minecraft.getInstance());
    }

    // ── PolyScreenEvents ──────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_OPENING.invoker().onScreenOpening(event.getNewScreen(), event.getCurrentScreen(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event)
    {
        PolyScreenEvents.SCREEN_CLOSING.invoker().onScreenClosing(event.getScreen());
    }

    @SubscribeEvent
    public static void onScreenOpened(ScreenEvent.Init.Post event)
    {
        net.minecraft.client.gui.screens.Screen s = event.getScreen();
        PolyScreenEvents.SCREEN_OPENED.invoker().onScreenOpened(s.getMinecraft(), s, s.width, s.height);
    }

    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event)
    {
        PolyScreenEvents.SCREEN_RENDER_PRE.invoker().onScreenRenderPre(
                event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event)
    {
        PolyScreenEvents.SCREEN_RENDER_POST.invoker().onScreenRenderPost(
                event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onScreenKeyPressedPre(ScreenEvent.KeyPressed.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_KEY_PRESS_ALLOW.invoker().onScreenKeyPressAllow(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_KEY_PRESS_BEFORE.invoker().onScreenKeyPressBefore(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onScreenKeyPressedPost(ScreenEvent.KeyPressed.Post event)
    {
        PolyScreenEvents.SCREEN_KEY_PRESS_AFTER.invoker().onScreenKeyPressAfter(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onScreenKeyReleasedPre(ScreenEvent.KeyReleased.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_KEY_RELEASE_ALLOW.invoker().onScreenKeyReleaseAllow(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_KEY_RELEASE_BEFORE.invoker().onScreenKeyReleaseBefore(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onScreenKeyReleasedPost(ScreenEvent.KeyReleased.Post event)
    {
        PolyScreenEvents.SCREEN_KEY_RELEASE_AFTER.invoker().onScreenKeyReleaseAfter(
                event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    @SubscribeEvent
    public static void onScreenCharTypedPre(ScreenEvent.CharacterTyped.Pre event)
    {
        CancelContext ctx = new CancelContext();
        // MC 26.1.2 CharacterEvent has no modifiers field — pass 0
        PolyScreenEvents.SCREEN_CHAR_TYPED_ALLOW.invoker().onScreenCharTypedAllow(
                event.getScreen(), (char) event.getCodePoint(), 0, ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onScreenCharTypedPost(ScreenEvent.CharacterTyped.Post event)
    {
        // MC 26.1.2 CharacterEvent has no modifiers field — pass 0
        PolyScreenEvents.SCREEN_CHAR_TYPED_AFTER.invoker().onScreenCharTypedAfter(
                event.getScreen(), (char) event.getCodePoint(), 0);
    }

    @SubscribeEvent
    public static void onScreenMouseClickPre(ScreenEvent.MouseButtonPressed.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_MOUSE_CLICK_ALLOW.invoker().onScreenMouseClickAllow(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_MOUSE_CLICK_BEFORE.invoker().onScreenMouseClickBefore(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenMouseClickPost(ScreenEvent.MouseButtonPressed.Post event)
    {
        PolyScreenEvents.SCREEN_MOUSE_CLICK_AFTER.invoker().onScreenMouseClickAfter(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenMouseReleasePre(ScreenEvent.MouseButtonReleased.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_MOUSE_RELEASE_ALLOW.invoker().onScreenMouseReleaseAllow(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_MOUSE_RELEASE_BEFORE.invoker().onScreenMouseReleaseBefore(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenMouseReleasePost(ScreenEvent.MouseButtonReleased.Post event)
    {
        PolyScreenEvents.SCREEN_MOUSE_RELEASE_AFTER.invoker().onScreenMouseReleaseAfter(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenMouseScrollPre(ScreenEvent.MouseScrolled.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_MOUSE_SCROLL_ALLOW.invoker().onScreenMouseScrollAllow(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_MOUSE_SCROLL_BEFORE.invoker().onScreenMouseScrollBefore(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onScreenMouseScrollPost(ScreenEvent.MouseScrolled.Post event)
    {
        PolyScreenEvents.SCREEN_MOUSE_SCROLL_AFTER.invoker().onScreenMouseScrollAfter(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onScreenMouseDragPre(ScreenEvent.MouseDragged.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyScreenEvents.SCREEN_MOUSE_DRAG_ALLOW.invoker().onScreenMouseDragAllow(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY(), ctx);
        if (ctx.isCancelled()) { event.setCanceled(true); return; }
        PolyScreenEvents.SCREEN_MOUSE_DRAG_BEFORE.invoker().onScreenMouseDragBefore(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }

    @SubscribeEvent
    public static void onScreenMouseDragPost(ScreenEvent.MouseDragged.Post event)
    {
        PolyScreenEvents.SCREEN_MOUSE_DRAG_AFTER.invoker().onScreenMouseDragAfter(
                event.getScreen(), event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }
}
