package net.creeperhost.polylib.containers.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

/**
 * Protocol adapter for synchronizing data between server and client containers.
 * Replaces the need for raw byte buffer manipulation and manual packet ID assignments.
 */
public interface ContainerSyncProtocol {

    /**
     * Sends a typed payload to the client-side container.
     */
    <T> void sendToClient(ServerPlayer player, int containerId, SyncPayloadType<T> type, T payload);

    /**
     * Sends a typed payload to the server-side container.
     */
    <T> void sendToServer(int containerId, SyncPayloadType<T> type, T payload);

    /**
     * Registers a handler for a specific payload type from the server.
     */
    <T> void registerClientHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler);

    /**
     * Registers a handler for a specific payload type from the client.
     */
    <T> void registerServerHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler);

    record SyncPayloadType<T>(Identifier id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {}
}
