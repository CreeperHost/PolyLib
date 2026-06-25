package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Client-side level load and unload events.
 */
public final class PolyClientLevelEvents
{
    /**
     * Fired when a client-side level (dimension) is loaded.
     * <p>
     * NeoForge: {@code LevelEvent.Load} (filtered for {@code ClientLevel})<br>
     * Fabric: {@code ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE} (level != null)
     */
    public static final PolyEvent<LevelLoad> CLIENT_LEVEL_LOAD = PolyEvent.create(
            handlers -> level -> handlers.forEach(h -> h.onLoad(level)));

    /**
     * Fired when a client-side level (dimension) is unloaded.
     * <p>
     * NeoForge: {@code LevelEvent.Unload} (filtered for {@code ClientLevel})<br>
     * Fabric: {@code ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE} (level == null)
     */
    public static final PolyEvent<LevelUnload> CLIENT_LEVEL_UNLOAD = PolyEvent.create(
            handlers -> level -> handlers.forEach(h -> h.onUnload(level)));

    private PolyClientLevelEvents() {}

    /**
     * Callback fired when a client level is loaded.
     */
    @FunctionalInterface
    public interface LevelLoad
    {
        void onLoad(ClientLevel level);
    }

    /**
     * Callback fired when a client level is unloaded.
     */
    @FunctionalInterface
    public interface LevelUnload
    {
        void onUnload(ClientLevel level);
    }
}
