package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

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

    @FunctionalInterface
    public interface LevelTickStart
    {
        void onTickStart(ClientLevel level);
    }

    @FunctionalInterface
    public interface TickStart
    {
        void onTickStart(Minecraft client);
    }

    @FunctionalInterface
    public interface TickEnd
    {
        void onTickEnd(Minecraft client);
    }

    @FunctionalInterface
    public interface LevelTickEnd
    {
        void onLevelTickEnd(ClientLevel level);
    }
}
