package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Client and client-level tick events.
 */
public final class PolyClientTickEvents
{
    /** Fired at the start of each client tick. */
    public static final PolyEvent<TickStart> CLIENT_TICK_START = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onTickStart(mc)));

    /** Fired at the end of each client tick. */
    public static final PolyEvent<TickEnd> CLIENT_TICK_END = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onTickEnd(mc)));

    /** Fired at the start of each client-side level (dimension) tick. */
    public static final PolyEvent<LevelTickStart> CLIENT_LEVEL_TICK_START = PolyEvent.create(
            handlers -> level -> handlers.forEach(h -> h.onTickStart(level)));

    /** Fired at the end of each client level (dimension) tick. */
    public static final PolyEvent<LevelTickEnd> CLIENT_LEVEL_TICK_END = PolyEvent.create(
            handlers -> level -> handlers.forEach(h -> h.onLevelTickEnd(level)));

    private PolyClientTickEvents()
    {
    }

    /**
     * Callback fired at the start of a client-level tick.
     */
    @FunctionalInterface
    public interface LevelTickStart
    {
        void onTickStart(ClientLevel level);
    }

    /**
     * Callback fired at the start of a client tick.
     */
    @FunctionalInterface
    public interface TickStart
    {
        void onTickStart(Minecraft client);
    }

    /**
     * Callback fired at the end of a client tick.
     */
    @FunctionalInterface
    public interface TickEnd
    {
        void onTickEnd(Minecraft client);
    }

    /**
     * Callback fired at the end of a client-level tick.
     */
    @FunctionalInterface
    public interface LevelTickEnd
    {
        void onLevelTickEnd(ClientLevel level);
    }
}
