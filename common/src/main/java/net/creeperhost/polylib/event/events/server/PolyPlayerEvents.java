package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.server.level.ServerPlayer;

public final class PolyPlayerEvents
{

    public static final PolyEvent<Login> LOGIN = PolyEvent.create(handlers -> player -> handlers.forEach(h -> h.onLogin(player)));
    public static final PolyEvent<Logout> LOGOUT = PolyEvent.create(handlers -> player -> handlers.forEach(h -> h.onLogout(player)));
    public static final PolyEvent<Respawn> RESPAWN = PolyEvent.create(handlers -> (player, endConquered) -> handlers.forEach(h -> h.onRespawn(player, endConquered)));
    public static final PolyEvent<StartTracking> START_TRACKING = PolyEvent.create(handlers -> (tracked, tracker) -> handlers.forEach(h -> h.onStartTracking(tracked, tracker)));

    private PolyPlayerEvents()
    {
    }

    @FunctionalInterface
    public interface Login
    {
        void onLogin(ServerPlayer player);
    }

    @FunctionalInterface
    public interface Logout
    {
        void onLogout(ServerPlayer player);
    }

    @FunctionalInterface
    public interface Respawn
    {
        void onRespawn(ServerPlayer player, boolean isEndConquered);
    }

    @FunctionalInterface
    public interface StartTracking
    {
        void onStartTracking(ServerPlayer tracked, ServerPlayer tracker);
    }
}
