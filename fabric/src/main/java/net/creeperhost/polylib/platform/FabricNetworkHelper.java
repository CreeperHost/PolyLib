package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsC2SPayload;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.network.OptionalPacketHandler;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.*;
import net.creeperhost.polylib.platform.services.INetworkHelper;
import net.creeperhost.polylib.player.serverdata.SyncPlayerServerDataS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingSyncS2CPayload;
import net.creeperhost.polylib.player.settings.UpdatePlayerClientSettingC2SPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FabricNetworkHelper implements INetworkHelper
{

    @Override
    public void init()
    {
        PayloadTypeRegistry.clientboundPlay().register(ContainerClientPayload.TYPE, ContainerClientPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TileDataClientPayload.TYPE, TileDataClientPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayerClientSettingSyncS2CPayload.TYPE, PlayerClientSettingSyncS2CPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncPlayerServerDataS2CPayload.TYPE, SyncPlayerServerDataS2CPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ContainerServerPayload.TYPE, ContainerServerPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TileDataServerPayload.TYPE, TileDataServerPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TilePacketServerPayload.TYPE, TilePacketServerPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AccessibilityPrefsC2SPayload.TYPE, AccessibilityPrefsC2SPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UpdatePlayerClientSettingC2SPayload.TYPE, UpdatePlayerClientSettingC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ContainerServerPayload.TYPE, (payload, context) ->
        {
            ServerPlayer player = context.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            context.server().execute(() -> PolyLibNetwork.handleContainerFromClient(player, buf));
        });
        ServerPlayNetworking.registerGlobalReceiver(TileDataServerPayload.TYPE, (payload, context) ->
        {
            ServerPlayer player = context.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            context.server().execute(() -> PolyLibNetwork.handleTileDataFromClient(player, buf));
        });
        ServerPlayNetworking.registerGlobalReceiver(TilePacketServerPayload.TYPE, (payload, context) ->
        {
            ServerPlayer player = context.player();
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()), player.registryAccess());
            context.server().execute(() -> PolyLibNetwork.handleTilePacketFromClient(player, buf));
        });
        ServerPlayNetworking.registerGlobalReceiver(AccessibilityPrefsC2SPayload.TYPE, (payload, context) ->
                context.server().execute(() -> AccessibilityPrefsManager.applyFromClient(
                        context.player().getUUID(), payload.values())));
        ServerPlayNetworking.registerGlobalReceiver(UpdatePlayerClientSettingC2SPayload.TYPE, (payload, context) ->
        {
            ServerPlayer sp = context.player();
            context.server().execute(() ->
                    PlayerClientSettingsManager.applyFromClient(sp, payload.typeId(), payload.data()));
        });
    }

    @Override
    public void initClient()
    {
        FabricClientNetwork.init();
    }

    @Override
    public <T extends CustomPacketPayload> void registerOptionalClientbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    @Override
    public <T extends CustomPacketPayload> void registerOptionalServerbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            OptionalPacketHandler<T> handler)
    {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                context.server().execute(() -> handler.handle(payload, context.player())));
    }

    @Override
    public <T extends CustomPacketPayload> void registerOptionalClientHandler(
            CustomPacketPayload.Type<T> type,
            OptionalPacketHandler<T> handler)
    {
        FabricClientNetwork.registerOptionalHandler(type, handler);
    }

    @Override
    public boolean canSendOptionalToServer(CustomPacketPayload.Type<?> type)
    {
        return FabricClientNetwork.canSend(type);
    }

    @Override
    public boolean canSendOptionalToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type)
    {
        return ServerPlayNetworking.canSend(player, type);
    }

    @Override
    public void sendToServer(CustomPacketPayload payload)
    {
        if (canSendOptionalToServer(payload.type()))
        {
            FabricClientNetwork.send(payload);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        if (canSendOptionalToPlayer(player, payload.type()))
        {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToPlayers(List<ServerPlayer> players, CustomPacketPayload payload)
    {
        for (ServerPlayer player : players)
        {
            sendToPlayer(player, payload);
        }
    }
}
