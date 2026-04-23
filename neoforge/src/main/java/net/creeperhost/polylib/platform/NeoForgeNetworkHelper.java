package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.*;
import net.creeperhost.polylib.platform.services.INetworkHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public class NeoForgeNetworkHelper implements INetworkHelper
{

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);

        registrar.playToServer(ContainerServerPayload.TYPE, ContainerServerPayload.CODEC, (payload, ctx) ->
        {
            Player player = ctx.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            ctx.enqueueWork(() -> PolyLibNetwork.handleContainerFromClient(player, buf));
        });
        registrar.playToServer(TileDataServerPayload.TYPE, TileDataServerPayload.CODEC, (payload, ctx) ->
        {
            Player player = ctx.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            ctx.enqueueWork(() -> PolyLibNetwork.handleTileDataFromClient(player, buf));
        });
        registrar.playToServer(TilePacketServerPayload.TYPE, TilePacketServerPayload.CODEC, (payload, ctx) ->
        {
            Player player = ctx.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            ctx.enqueueWork(() -> PolyLibNetwork.handleTilePacketFromClient(player, buf));
        });

        registrar.playToClient(ContainerClientPayload.TYPE, ContainerClientPayload.CODEC, (payload, ctx) ->
        {
            Player player = ctx.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            ctx.enqueueWork(() -> PolyLibNetwork.handleContainerFromServer(player, buf));
        });
        registrar.playToClient(TileDataClientPayload.TYPE, TileDataClientPayload.CODEC, (payload, ctx) ->
        {
            Player player = ctx.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            ctx.enqueueWork(() -> PolyLibNetwork.handleTileDataValueFromServer(player, buf));
        });
    }

    @Override
    public void init()
    {
        // Handled via RegisterPayloadsEvent — see PolyLib#onRegisterPayloads
    }

    @Override
    public void initClient()
    {
        // Handled via RegisterPayloadsEvent — see PolyLib#onRegisterPayloads
    }

    @Override
    public void sendToServer(CustomPacketPayload payload)
    {
        ClientPacketDistributor.sendToServer(payload);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToPlayers(List<ServerPlayer> players, CustomPacketPayload payload)
    {
        for (ServerPlayer player : players)
        {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

}
