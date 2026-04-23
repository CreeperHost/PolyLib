package net.creeperhost.polylib.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface INetworkHelper {
    void init();
    void initClient();
    void sendToServer(CustomPacketPayload payload);
    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
    void sendToPlayers(List<ServerPlayer> players, CustomPacketPayload payload);
}
