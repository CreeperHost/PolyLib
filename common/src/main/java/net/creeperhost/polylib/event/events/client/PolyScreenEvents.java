package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * Client screen lifecycle, rendering, keyboard, mouse, and text-input events.
 * <p>
 * The {@code Allow} callbacks receive a {@link CancelContext}; cancelling them
 * suppresses the matching vanilla screen input action.
 */
public final class PolyScreenEvents {

    // Lifecycle
    public static final PolyEvent<ScreenOpening> SCREEN_OPENING = PolyEvent.create(
            handlers -> (screen, oldScreen, ctx) -> {
                for (var h : handlers) {
                    h.onScreenOpening(screen, oldScreen, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenOpened> SCREEN_OPENED = PolyEvent.create(
            handlers -> (mc, screen, width, height) ->
                    handlers.forEach(h -> h.onScreenOpened(mc, screen, width, height)));

    public static final PolyEvent<ScreenClosing> SCREEN_CLOSING = PolyEvent.create(
            handlers -> screen -> handlers.forEach(h -> h.onScreenClosing(screen)));

    // Tick & Render
    public static final PolyEvent<ScreenTickBefore> SCREEN_TICK_BEFORE = PolyEvent.create(
            handlers -> screen -> handlers.forEach(h -> h.onScreenTickBefore(screen)));

    public static final PolyEvent<ScreenTickAfter> SCREEN_TICK_AFTER = PolyEvent.create(
            handlers -> screen -> handlers.forEach(h -> h.onScreenTickAfter(screen)));

    public static final PolyEvent<ScreenRenderPre> SCREEN_RENDER_PRE = PolyEvent.create(
            handlers -> (screen, GuiGraphicsExtractor, mouseX, mouseY, partialTick) ->
                    handlers.forEach(h -> h.onScreenRenderPre(screen, GuiGraphicsExtractor, mouseX, mouseY, partialTick)));

    public static final PolyEvent<ScreenRenderPost> SCREEN_RENDER_POST = PolyEvent.create(
            handlers -> (screen, GuiGraphicsExtractor, mouseX, mouseY, partialTick) ->
                    handlers.forEach(h -> h.onScreenRenderPost(screen, GuiGraphicsExtractor, mouseX, mouseY, partialTick)));

    // Keyboard
    public static final PolyEvent<ScreenKeyPressAllow> SCREEN_KEY_PRESS_ALLOW = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers, ctx) -> {
                for (var h : handlers) {
                    h.onScreenKeyPressAllow(screen, keyCode, scanCode, modifiers, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenKeyPressBefore> SCREEN_KEY_PRESS_BEFORE = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers) ->
                    handlers.forEach(h -> h.onScreenKeyPressBefore(screen, keyCode, scanCode, modifiers)));

    public static final PolyEvent<ScreenKeyPressAfter> SCREEN_KEY_PRESS_AFTER = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers) ->
                    handlers.forEach(h -> h.onScreenKeyPressAfter(screen, keyCode, scanCode, modifiers)));

    public static final PolyEvent<ScreenKeyReleaseAllow> SCREEN_KEY_RELEASE_ALLOW = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers, ctx) -> {
                for (var h : handlers) {
                    h.onScreenKeyReleaseAllow(screen, keyCode, scanCode, modifiers, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenKeyReleaseBefore> SCREEN_KEY_RELEASE_BEFORE = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers) ->
                    handlers.forEach(h -> h.onScreenKeyReleaseBefore(screen, keyCode, scanCode, modifiers)));

    public static final PolyEvent<ScreenKeyReleaseAfter> SCREEN_KEY_RELEASE_AFTER = PolyEvent.create(
            handlers -> (screen, keyCode, scanCode, modifiers) ->
                    handlers.forEach(h -> h.onScreenKeyReleaseAfter(screen, keyCode, scanCode, modifiers)));

    public static final PolyEvent<ScreenCharTypedAllow> SCREEN_CHAR_TYPED_ALLOW = PolyEvent.create(
            handlers -> (screen, codePoint, modifiers, ctx) -> {
                for (var h : handlers) {
                    h.onScreenCharTypedAllow(screen, codePoint, modifiers, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenCharTypedAfter> SCREEN_CHAR_TYPED_AFTER = PolyEvent.create(
            handlers -> (screen, codePoint, modifiers) ->
                    handlers.forEach(h -> h.onScreenCharTypedAfter(screen, codePoint, modifiers)));

    // Mouse
    public static final PolyEvent<ScreenMouseClickAllow> SCREEN_MOUSE_CLICK_ALLOW = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button, ctx) -> {
                for (var h : handlers) {
                    h.onScreenMouseClickAllow(screen, mouseX, mouseY, button, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenMouseClickBefore> SCREEN_MOUSE_CLICK_BEFORE = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button) ->
                    handlers.forEach(h -> h.onScreenMouseClickBefore(screen, mouseX, mouseY, button)));

    public static final PolyEvent<ScreenMouseClickAfter> SCREEN_MOUSE_CLICK_AFTER = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button) ->
                    handlers.forEach(h -> h.onScreenMouseClickAfter(screen, mouseX, mouseY, button)));

    public static final PolyEvent<ScreenMouseReleaseAllow> SCREEN_MOUSE_RELEASE_ALLOW = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button, ctx) -> {
                for (var h : handlers) {
                    h.onScreenMouseReleaseAllow(screen, mouseX, mouseY, button, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenMouseReleaseBefore> SCREEN_MOUSE_RELEASE_BEFORE = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button) ->
                    handlers.forEach(h -> h.onScreenMouseReleaseBefore(screen, mouseX, mouseY, button)));

    public static final PolyEvent<ScreenMouseReleaseAfter> SCREEN_MOUSE_RELEASE_AFTER = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button) ->
                    handlers.forEach(h -> h.onScreenMouseReleaseAfter(screen, mouseX, mouseY, button)));

    public static final PolyEvent<ScreenMouseScrollAllow> SCREEN_MOUSE_SCROLL_ALLOW = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, scrollX, scrollY, ctx) -> {
                for (var h : handlers) {
                    h.onScreenMouseScrollAllow(screen, mouseX, mouseY, scrollX, scrollY, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenMouseScrollBefore> SCREEN_MOUSE_SCROLL_BEFORE = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, scrollX, scrollY) ->
                    handlers.forEach(h -> h.onScreenMouseScrollBefore(screen, mouseX, mouseY, scrollX, scrollY)));

    public static final PolyEvent<ScreenMouseScrollAfter> SCREEN_MOUSE_SCROLL_AFTER = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, scrollX, scrollY) ->
                    handlers.forEach(h -> h.onScreenMouseScrollAfter(screen, mouseX, mouseY, scrollX, scrollY)));

    public static final PolyEvent<ScreenMouseDragAllow> SCREEN_MOUSE_DRAG_ALLOW = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button, dragX, dragY, ctx) -> {
                for (var h : handlers) {
                    h.onScreenMouseDragAllow(screen, mouseX, mouseY, button, dragX, dragY, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    public static final PolyEvent<ScreenMouseDragBefore> SCREEN_MOUSE_DRAG_BEFORE = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button, dragX, dragY) ->
                    handlers.forEach(h -> h.onScreenMouseDragBefore(screen, mouseX, mouseY, button, dragX, dragY)));

    public static final PolyEvent<ScreenMouseDragAfter> SCREEN_MOUSE_DRAG_AFTER = PolyEvent.create(
            handlers -> (screen, mouseX, mouseY, button, dragX, dragY) ->
                    handlers.forEach(h -> h.onScreenMouseDragAfter(screen, mouseX, mouseY, button, dragX, dragY)));

    private PolyScreenEvents() {}

    @FunctionalInterface
    public interface ScreenOpening {
        void onScreenOpening(@Nullable Screen screen, @Nullable Screen oldScreen, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenOpened {
        void onScreenOpened(Minecraft mc, Screen screen, int width, int height);
    }

    @FunctionalInterface
    public interface ScreenClosing {
        void onScreenClosing(Screen screen);
    }

    @FunctionalInterface
    public interface ScreenTickBefore {
        void onScreenTickBefore(Screen screen);
    }

    @FunctionalInterface
    public interface ScreenTickAfter {
        void onScreenTickAfter(Screen screen);
    }

    @FunctionalInterface
    public interface ScreenRenderPre {
        void onScreenRenderPre(Screen screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface ScreenRenderPost {
        void onScreenRenderPost(Screen screen, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick);
    }

    @FunctionalInterface
    public interface ScreenKeyPressAllow {
        void onScreenKeyPressAllow(Screen screen, int keyCode, int scanCode, int modifiers, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenKeyPressBefore {
        void onScreenKeyPressBefore(Screen screen, int keyCode, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface ScreenKeyPressAfter {
        void onScreenKeyPressAfter(Screen screen, int keyCode, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface ScreenKeyReleaseAllow {
        void onScreenKeyReleaseAllow(Screen screen, int keyCode, int scanCode, int modifiers, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenKeyReleaseBefore {
        void onScreenKeyReleaseBefore(Screen screen, int keyCode, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface ScreenKeyReleaseAfter {
        void onScreenKeyReleaseAfter(Screen screen, int keyCode, int scanCode, int modifiers);
    }

    @FunctionalInterface
    public interface ScreenCharTypedAllow {
        void onScreenCharTypedAllow(Screen screen, char codePoint, int modifiers, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenCharTypedAfter {
        void onScreenCharTypedAfter(Screen screen, char codePoint, int modifiers);
    }

    @FunctionalInterface
    public interface ScreenMouseClickAllow {
        void onScreenMouseClickAllow(Screen screen, double mouseX, double mouseY, int button, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenMouseClickBefore {
        void onScreenMouseClickBefore(Screen screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface ScreenMouseClickAfter {
        void onScreenMouseClickAfter(Screen screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface ScreenMouseReleaseAllow {
        void onScreenMouseReleaseAllow(Screen screen, double mouseX, double mouseY, int button, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenMouseReleaseBefore {
        void onScreenMouseReleaseBefore(Screen screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface ScreenMouseReleaseAfter {
        void onScreenMouseReleaseAfter(Screen screen, double mouseX, double mouseY, int button);
    }

    @FunctionalInterface
    public interface ScreenMouseScrollAllow {
        void onScreenMouseScrollAllow(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenMouseScrollBefore {
        void onScreenMouseScrollBefore(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY);
    }

    @FunctionalInterface
    public interface ScreenMouseScrollAfter {
        void onScreenMouseScrollAfter(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY);
    }

    @FunctionalInterface
    public interface ScreenMouseDragAllow {
        void onScreenMouseDragAllow(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ScreenMouseDragBefore {
        void onScreenMouseDragBefore(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    @FunctionalInterface
    public interface ScreenMouseDragAfter {
        void onScreenMouseDragAfter(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }
}
