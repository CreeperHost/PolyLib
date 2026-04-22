package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHelper implements INetworkHelper
{
    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload message)
    {
        ServerPlayNetworking.send(player, message);
    }
}
