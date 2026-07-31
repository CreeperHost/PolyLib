package net.creeperhost.polylib.network;

import net.creeperhost.polylib.platform.Services;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Cross-loader registration and transport for optional play-stage packets.
 * <p>
 * Optional packets do not make the packet-owning mod a connection requirement.
 * A payload is sent only when the remote connection advertised support for its
 * type. Registration must happen during normal mod initialization, before a
 * server starts or a client connects.
 */
public final class OptionalPackets
{
    private OptionalPackets() {}

    /**
     * Registers a payload sent from the server to a client.
     * <p>
     * Register the matching client handler separately from client initialization
     * with {@link #registerClientHandler(CustomPacketPayload.Type, OptionalPacketHandler)}.
     *
     * @param type  payload type
     * @param codec payload codec
     * @param <T>   payload class
     */
    public static <T extends CustomPacketPayload> void registerClientbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        Services.NETWORK.registerOptionalClientbound(type, codec);
    }

    /**
     * Registers a payload sent from a client to the server and its server handler.
     *
     * @param type    payload type
     * @param codec   payload codec
     * @param handler handler invoked on the server thread
     * @param <T>     payload class
     */
    public static <T extends CustomPacketPayload> void registerServerbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            OptionalPacketHandler<T> handler)
    {
        Services.NETWORK.registerOptionalServerbound(type, codec, handler);
    }

    /**
     * Registers the client handler for a previously registered clientbound payload.
     * This method must only be called from physical-client initialization.
     *
     * @param type    payload type
     * @param handler handler invoked on the client thread
     * @param <T>     payload class
     */
    public static <T extends CustomPacketPayload> void registerClientHandler(
            CustomPacketPayload.Type<T> type,
            OptionalPacketHandler<T> handler)
    {
        Services.NETWORK.registerOptionalClientHandler(type, handler);
    }

    /**
     * Checks whether the connected server accepts the payload type.
     *
     * @param type payload type
     * @return {@code true} when connected and the server advertised support
     */
    public static boolean canSendToServer(CustomPacketPayload.Type<?> type)
    {
        return Services.NETWORK.canSendOptionalToServer(type);
    }

    /**
     * Sends a payload to the server when the server advertised support for it.
     *
     * @param payload payload to send
     * @return {@code true} when sent, or {@code false} when unsupported or disconnected
     */
    public static boolean sendToServer(CustomPacketPayload payload)
    {
        if (!canSendToServer(payload.type()))
        {
            return false;
        }
        Services.NETWORK.sendToServer(payload);
        return true;
    }

    /**
     * Checks whether a player's client accepts the payload type.
     *
     * @param player target player
     * @param type   payload type
     * @return {@code true} when the client advertised support
     */
    public static boolean canSendToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type)
    {
        return Services.NETWORK.canSendOptionalToPlayer(player, type);
    }

    /**
     * Sends a payload to one player when that client advertised support for it.
     *
     * @param player  target player
     * @param payload payload to send
     * @return {@code true} when sent, or {@code false} when unsupported
     */
    public static boolean sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        if (!canSendToPlayer(player, payload.type()))
        {
            return false;
        }
        Services.NETWORK.sendToPlayer(player, payload);
        return true;
    }

    /**
     * Sends a payload to every player whose client advertised support.
     *
     * @param players target players
     * @param payload payload to send
     * @return number of players the payload was sent to
     */
    public static int sendToPlayers(List<ServerPlayer> players, CustomPacketPayload payload)
    {
        int sent = 0;
        for (ServerPlayer player : players)
        {
            if (sendToPlayer(player, payload))
            {
                sent++;
            }
        }
        return sent;
    }
}
