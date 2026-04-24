package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.server.level.ServerPlayer;

public final class PolyPlayerTickEvents
{
    /** Fired at the start of each server-side player tick. */
    public static final PolyEvent<Tick> PLAYER_TICK_START = PolyEvent.create(
            handlers -> player -> handlers.forEach(h -> h.onTick(player)));

    /** Fired at the end of each server-side player tick. */
    public static final PolyEvent<Tick> PLAYER_TICK_END = PolyEvent.create(
            handlers -> player -> handlers.forEach(h -> h.onTick(player)));

    private PolyPlayerTickEvents()
    {
    }

    @FunctionalInterface
    public interface Tick
    {
        void onTick(ServerPlayer player);
    }
}
