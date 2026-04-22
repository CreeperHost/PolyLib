package net.creeperhost.polylib.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper
{
    void sendToPlayer(ServerPlayer player, CustomPacketPayload message);
}
