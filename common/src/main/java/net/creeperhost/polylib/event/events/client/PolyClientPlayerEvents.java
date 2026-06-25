package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client-side local player connection events.
 */
public final class PolyClientPlayerEvents
{
    /**
     * Fired when the local player joins a server or world.
     * <p>
     * NeoForge: {@code ClientPlayerNetworkEvent.LoggingIn}<br>
     * Fabric: {@code ClientPlayConnectionEvents.JOIN}
     */
    public static final PolyEvent<Login> CLIENT_LOGIN = PolyEvent.create(
            handlers -> player -> handlers.forEach(h -> h.onLogin(player)));

    /**
     * Fired when the local player is logging out of a server or world.
     * <p>
     * NeoForge: {@code ClientPlayerNetworkEvent.LoggingOut}<br>
     * Fabric: {@code ClientPlayConnectionEvents.DISCONNECT}
     */
    public static final PolyEvent<Logout> LOGOUT = PolyEvent.create(
            handlers -> player -> handlers.forEach(h -> h.onLogout(player)));

    private PolyClientPlayerEvents()
    {
    }

    /**
     * Callback fired when the local player logs in.
     */
    @FunctionalInterface
    public interface Login
    {
        void onLogin(LocalPlayer player);
    }

    /**
     * Callback fired when the local player logs out.
     */
    @FunctionalInterface
    public interface Logout
    {
        void onLogout(LocalPlayer player);
    }
}
