package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;

/**
 * Client input events for keyboard and mouse button activity.
 */
public final class PolyInputEvents
{
    /**
     * Fired when a keyboard key is pressed, released, or repeated.
     * {@code action}: 0 = release, 1 = press, 2 = repeat.
     * <p>
     * NeoForge: {@code InputEvent.Key}<br>
     * Fabric: mixin into {@code KeyboardHandler}
     */
    public static final PolyEvent<KeyInput> INPUT_KEY = PolyEvent.create(
            handlers -> (key, scanCode, action, modifiers) -> handlers.forEach(h -> h.onKeyInput(key, scanCode, action, modifiers)));

    /**
     * Fired when a mouse button is pressed or released.
     * {@code action}: 0 = release, 1 = press.
     * <p>
     * NeoForge: {@code InputEvent.MouseButton.Post}<br>
     * Fabric: mixin into {@code MouseHandler}
     */
    public static final PolyEvent<MouseInput> INPUT_MOUSE = PolyEvent.create(
            handlers -> (button, action, modifiers) -> handlers.forEach(h -> h.onMouseInput(button, action, modifiers)));

    private PolyInputEvents() {}

    /**
     * Callback fired for keyboard key input.
     */
    @FunctionalInterface
    public interface KeyInput
    {
        void onKeyInput(int key, int scanCode, int action, int modifiers);
    }

    /**
     * Callback fired for mouse button input.
     */
    @FunctionalInterface
    public interface MouseInput
    {
        void onMouseInput(int button, int action, int modifiers);
    }
}
