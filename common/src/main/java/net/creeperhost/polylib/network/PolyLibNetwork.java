package net.creeperhost.polylib.network;

import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PolyLibNetwork
{
    //Server To Client
    private static final Identifier CONTAINER_PACKET_TO_CLIENT = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "container_client");
    private static final Identifier TILE_DATA_VALUE_TO_CLIENT = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tile_client");

    //Client to server
    private static final Identifier CONTAINER_PACKET_TO_SERVER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "container_packet_server");
    private static final Identifier TILE_DATA_VALUE_TO_SERVER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tile_data_server");
    private static final Identifier TILE_PACKET_TO_SERVER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tile_packet_server");

    public static void init()
    {
        if (Services.PLATFORM.isClient())
        {
            //TODO registerReceiver
        } else {

        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload message){
        Services.NETWORK.sendToPlayer(player, message);
    }
}
