package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.ContainerClientPayload;
import net.creeperhost.polylib.network.packets.TileDataClientPayload;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataClientCache;
import net.creeperhost.polylib.player.serverdata.SyncPlayerServerDataS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingSyncS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsClientCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/** Keeps Fabric client networking classes out of the dedicated-server service implementation. */
final class FabricClientNetworkAccess
{
    private FabricClientNetworkAccess()
    {
    }

    static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(ContainerClientPayload.TYPE, (payload, context) ->
        {
            Player player = context.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            context.client().execute(() -> PolyLibNetwork.handleContainerFromServer(player, buf));
        });
        ClientPlayNetworking.registerGlobalReceiver(TileDataClientPayload.TYPE, (payload, context) ->
        {
            Player player = context.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            context.client().execute(() -> PolyLibNetwork.handleTileDataValueFromServer(player, buf));
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerClientSettingSyncS2CPayload.TYPE, (payload, context) ->
                context.client().execute(() ->
                        PlayerClientSettingsClientCache.receive(payload.playerUUID(), payload.typeId(), payload.data())));
        ClientPlayNetworking.registerGlobalReceiver(SyncPlayerServerDataS2CPayload.TYPE, (payload, context) ->
                context.client().execute(() ->
                        PlayerServerDataClientCache.receive(payload.typeId(), payload.data())));
    }

    static void send(CustomPacketPayload payload)
    {
        if (ClientPlayNetworking.canSend(payload.type()))
        {
            ClientPlayNetworking.send(payload);
        }
    }
}
