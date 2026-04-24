package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;

import org.jetbrains.annotations.Nullable;

/**
 * Client-side connection lifecycle events (play, login, configuration channels).
 * <p>
 * {@code @Nullable Object sender} parameters accept a Fabric {@code PacketSender} on Fabric;
 * NeoForge adapters pass {@code null}.
 */
public final class PolyClientConnectionEvents
{
    // -------------------------------------------------------------------------
    // Play channel
    // -------------------------------------------------------------------------

    /** Fired when the {@code ClientPacketListener} (play connection) is first initialised. */
    public static final PolyEvent<PlayInit> CLIENT_PLAY_INIT = PolyEvent.create(
            handlers -> (handler, sender, client) -> handlers.forEach(h -> h.onPlayInit(handler, sender, client)));

    /** Fired when the client successfully joins the play phase. */
    public static final PolyEvent<PlayJoin> CLIENT_PLAY_JOIN = PolyEvent.create(
            handlers -> (handler, sender, client) -> handlers.forEach(h -> h.onPlayJoin(handler, sender, client)));

    /** Fired when the client disconnects from the play phase. */
    public static final PolyEvent<PlayDisconnect> CLIENT_PLAY_DISCONNECT = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onPlayDisconnect(handler, client)));

    // -------------------------------------------------------------------------
    // Login channel
    // -------------------------------------------------------------------------

    /** Fired when the login handshake listener is constructed ({@code ClientHandshakePacketListenerImpl}). */
    public static final PolyEvent<LoginInit> CLIENT_LOGIN_INIT = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onLoginInit(handler, client)));

    /** Fired when the server sends a custom login query packet. */
    public static final PolyEvent<LoginQueryStart> CLIENT_LOGIN_QUERY_START = PolyEvent.create(
            handlers -> (handler, client, sender, payload) ->
                    handlers.forEach(h -> h.onQueryStart(handler, client, sender, payload)));

    /**
     * Fired when the client sends a response to a custom login query packet.
     * <p>
     * Fabric: mixin on {@code ClientHandshakePacketListenerImpl#handleCustomQuery} at RETURN.<br>
     * NeoForge: mixin on same method.
     */
    public static final PolyEvent<LoginQueryResponse> CLIENT_LOGIN_QUERY_RESPONSE = PolyEvent.create(
            handlers -> (handler, client, payload) ->
                    handlers.forEach(h -> h.onQueryResponse(handler, client, payload)));

    /** Fired when the login connection is disconnected. */
    public static final PolyEvent<LoginDisconnect> CLIENT_LOGIN_DISCONNECT = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onLoginDisconnect(handler, client)));

    // -------------------------------------------------------------------------
    // Configuration channel
    // -------------------------------------------------------------------------

    /** Fired when the configuration connection listener is constructed. */
    public static final PolyEvent<ConfigurationInit> CLIENT_CONFIGURATION_INIT = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onConfigurationInit(handler, client)));

    /** Fired when configuration is complete and the client is transitioning to play. */
    public static final PolyEvent<ConfigurationComplete> CLIENT_CONFIGURATION_COMPLETE = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onConfigurationComplete(handler, client)));

    /** Fired when the configuration connection is disconnected. */
    public static final PolyEvent<ConfigurationDisconnect> CLIENT_CONFIGURATION_DISCONNECT = PolyEvent.create(
            handlers -> (handler, client) -> handlers.forEach(h -> h.onConfigurationDisconnect(handler, client)));

    private PolyClientConnectionEvents() {}

    @FunctionalInterface
    public interface PlayInit
    {
        void onPlayInit(ClientPacketListener handler, @Nullable Object sender, Minecraft client);
    }

    @FunctionalInterface
    public interface PlayJoin
    {
        void onPlayJoin(ClientPacketListener handler, @Nullable Object sender, Minecraft client);
    }

    @FunctionalInterface
    public interface PlayDisconnect
    {
        void onPlayDisconnect(ClientPacketListener handler, Minecraft client);
    }

    @FunctionalInterface
    public interface LoginInit
    {
        void onLoginInit(ClientHandshakePacketListenerImpl handler, Minecraft client);
    }

    @FunctionalInterface
    public interface LoginQueryStart
    {
        void onQueryStart(ClientHandshakePacketListenerImpl handler, Minecraft client,
                          @Nullable Object sender, @Nullable CustomQueryPayload payload);
    }

    @FunctionalInterface
    public interface LoginQueryResponse
    {
        void onQueryResponse(ClientHandshakePacketListenerImpl handler, Minecraft client,
                             CustomQueryPayload payload);
    }

    @FunctionalInterface
    public interface LoginDisconnect
    {
        void onLoginDisconnect(ClientHandshakePacketListenerImpl handler, Minecraft client);
    }

    @FunctionalInterface
    public interface ConfigurationInit
    {
        void onConfigurationInit(ClientConfigurationPacketListenerImpl handler, Minecraft client);
    }

    @FunctionalInterface
    public interface ConfigurationComplete
    {
        void onConfigurationComplete(ClientConfigurationPacketListenerImpl handler, Minecraft client);
    }

    @FunctionalInterface
    public interface ConfigurationDisconnect
    {
        void onConfigurationDisconnect(ClientConfigurationPacketListenerImpl handler, Minecraft client);
    }
}
