package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.io.File;

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

    @FunctionalInterface
    public interface RenderGuiPre {
        void onRenderGuiPre(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderGuiPost {
        void onRenderGuiPost(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick);
    }

    @FunctionalInterface
    public interface RenderHudLayerPre {
        void onRenderHudLayerPre(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, Identifier layerId, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderHudLayerPost {
        void onRenderHudLayerPost(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, Identifier layerId);
    }

    @FunctionalInterface
    public interface CustomizeHudOverlay {
        void onCustomizeHudOverlay(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, String type, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ContainerScreenRenderBg {
        void onContainerScreenRenderBg(AbstractContainerScreen<?> screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY);
    }

    @FunctionalInterface
    public interface ContainerScreenRenderFg {
        void onContainerScreenRenderFg(AbstractContainerScreen<?> screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY);
    }

    @FunctionalInterface
    public interface ToastAdd {
        void onToastAdd(Toast toast, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Screenshot {
        void onScreenshot(File screenshotFile, CancelContext ctx);
    }

    @FunctionalInterface
    public interface PlayerHeartTypeEvent {
        void onPlayerHeartType(Player player, PolyHeartType[] result);
    }
}
