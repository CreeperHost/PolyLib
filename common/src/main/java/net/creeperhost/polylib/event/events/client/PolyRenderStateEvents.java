package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;

/**
 * Events related to client render state validity.
 */
public final class PolyRenderStateEvents
{
    /**
     * Fired when the client render state must be invalidated (resource reload,
     * model rebuild, graphics settings change, etc.). Listeners should release
     * any caches that reference baked models or texture-atlas sprites.
     * <p>
     * NeoForge: mixin on {@code LevelRenderer#allChanged} at RETURN<br>
     * Fabric: {@code InvalidateRenderStateCallback.EVENT}
     */
    public static final PolyEvent<Invalidate> INVALIDATE_RENDER_STATE = PolyEvent.create(
            handlers -> () -> handlers.forEach(Invalidate::onInvalidate));

    private PolyRenderStateEvents() {}

    @FunctionalInterface
    public interface Invalidate
    {
        void onInvalidate();
    }
}
