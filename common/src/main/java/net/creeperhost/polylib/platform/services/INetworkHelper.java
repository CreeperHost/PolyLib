package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.network.OptionalPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface INetworkHelper {
    void init();
    void initClient();
    <T extends CustomPacketPayload> void registerOptionalClientbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec);
    <T extends CustomPacketPayload> void registerOptionalServerbound(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, OptionalPacketHandler<T> handler);
    <T extends CustomPacketPayload> void registerOptionalClientHandler(CustomPacketPayload.Type<T> type, OptionalPacketHandler<T> handler);
    boolean canSendOptionalToServer(CustomPacketPayload.Type<?> type);
    boolean canSendOptionalToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type);
    void sendToServer(CustomPacketPayload payload);
    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
    void sendToPlayers(List<ServerPlayer> players, CustomPacketPayload payload);
}
