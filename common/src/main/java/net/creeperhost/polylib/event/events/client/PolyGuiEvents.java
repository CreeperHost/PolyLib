package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.io.File;

/**
 * Client GUI and HUD rendering events.
 * <p>
 * Events ending in {@code Pre} or carrying a {@link CancelContext} may be cancelled
 * to suppress the corresponding GUI action or render layer.
 */
public final class PolyGuiEvents {

    public static final PolyEvent<RenderGuiPre> RENDER_GUI_PRE = PolyEvent.create(
            handlers -> (GuiGraphicsExtractor, partialTick, ctx) -> {
                for (var h : handlers) {
                    h.onRenderGuiPre(GuiGraphicsExtractor, partialTick, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<RenderGuiPost> RENDER_GUI_POST = PolyEvent.create(
            handlers -> (GuiGraphicsExtractor, partialTick) ->
                    handlers.forEach(h -> h.onRenderGuiPost(GuiGraphicsExtractor, partialTick)));

    public static final PolyEvent<RenderHudLayerPre> RENDER_HUD_LAYER_PRE = PolyEvent.create(
            handlers -> (GuiGraphicsExtractor, partialTick, layerId, ctx) -> {
                for (var h : handlers) {
                    h.onRenderHudLayerPre(GuiGraphicsExtractor, partialTick, layerId, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<RenderHudLayerPost> RENDER_HUD_LAYER_POST = PolyEvent.create(
            handlers -> (GuiGraphicsExtractor, partialTick, layerId) ->
                    handlers.forEach(h -> h.onRenderHudLayerPost(GuiGraphicsExtractor, partialTick, layerId)));

    public static final PolyEvent<CustomizeHudOverlay> CUSTOMIZE_HUD_OVERLAY = PolyEvent.create(
            handlers -> (GuiGraphicsExtractor, partialTick, type, ctx) -> {
                for (var h : handlers) {
                    h.onCustomizeHudOverlay(GuiGraphicsExtractor, partialTick, type, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ContainerScreenRenderBg> CONTAINER_SCREEN_RENDER_BG = PolyEvent.create(
            handlers -> (screen, GuiGraphicsExtractor, mouseX, mouseY) ->
                    handlers.forEach(h -> h.onContainerScreenRenderBg(screen, GuiGraphicsExtractor, mouseX, mouseY)));

    public static final PolyEvent<ContainerScreenRenderFg> CONTAINER_SCREEN_RENDER_FG = PolyEvent.create(
            handlers -> (screen, GuiGraphicsExtractor, mouseX, mouseY) ->
                    handlers.forEach(h -> h.onContainerScreenRenderFg(screen, GuiGraphicsExtractor, mouseX, mouseY)));

    public static final PolyEvent<ToastAdd> TOAST_ADD = PolyEvent.create(
            handlers -> (toast, ctx) -> {
                for (var h : handlers) {
                    h.onToastAdd(toast, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<Screenshot> SCREENSHOT = PolyEvent.create(
            handlers -> (screenshotFile, ctx) -> {
                for (var h : handlers) {
                    h.onScreenshot(screenshotFile, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<PlayerHeartTypeEvent> PLAYER_HEART_TYPE = PolyEvent.create(
            handlers -> (player, result) ->
                    handlers.forEach(h -> h.onPlayerHeartType(player, result)));

    private PolyGuiEvents() {}

    /**
     * Callback fired before the GUI is rendered.
     */
    @FunctionalInterface
    public interface RenderGuiPre {
        void onRenderGuiPre(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, CancelContext ctx);
    }

    /**
     * Callback fired after the GUI is rendered.
     */
    @FunctionalInterface
    public interface RenderGuiPost {
        void onRenderGuiPost(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick);
    }

    /**
     * Callback fired before a specific HUD layer is rendered.
     */
    @FunctionalInterface
    public interface RenderHudLayerPre {
        void onRenderHudLayerPre(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, Identifier layerId, CancelContext ctx);
    }

    /**
     * Callback fired after a specific HUD layer is rendered.
     */
    @FunctionalInterface
    public interface RenderHudLayerPost {
        void onRenderHudLayerPost(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, Identifier layerId);
    }

    /**
     * Callback for suppressing or customizing named HUD overlays.
     */
    @FunctionalInterface
    public interface CustomizeHudOverlay {
        void onCustomizeHudOverlay(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, String type, CancelContext ctx);
    }

    /**
     * Callback fired while a container screen background is rendered.
     */
    @FunctionalInterface
    public interface ContainerScreenRenderBg {
        void onContainerScreenRenderBg(AbstractContainerScreen<?> screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY);
    }

    /**
     * Callback fired while a container screen foreground is rendered.
     */
    @FunctionalInterface
    public interface ContainerScreenRenderFg {
        void onContainerScreenRenderFg(AbstractContainerScreen<?> screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY);
    }

    /**
     * Callback fired before a toast is added.
     */
    @FunctionalInterface
    public interface ToastAdd {
        void onToastAdd(Toast toast, CancelContext ctx);
    }

    /**
     * Callback fired when a screenshot is captured.
     */
    @FunctionalInterface
    public interface Screenshot {
        void onScreenshot(File screenshotFile, CancelContext ctx);
    }

    /**
     * Callback for overriding the heart type used when rendering a player's health.
     */
    @FunctionalInterface
    public interface PlayerHeartTypeEvent {
        void onPlayerHeartType(Player player, PolyHeartType[] result);
    }
}
