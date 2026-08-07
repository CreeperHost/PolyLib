package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsC2SPayload;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.*;
import net.creeperhost.polylib.platform.services.INetworkHelper;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataClientCache;
import net.creeperhost.polylib.player.serverdata.SyncPlayerServerDataS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsClientCache;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingSyncS2CPayload;
import net.creeperhost.polylib.player.settings.UpdatePlayerClientSettingC2SPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public class NeoForgeNetworkHelper implements INetworkHelper
{

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event)
    {
        PayloadRegistrar registrar = event.registrar(Constants.MOD_ID).optional();

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
        registrar.playToClient(PlayerClientSettingSyncS2CPayload.TYPE, PlayerClientSettingSyncS2CPayload.CODEC, (payload, ctx) ->
                ctx.enqueueWork(() -> PlayerClientSettingsClientCache.receive(
                        payload.playerUUID(), payload.typeId(), payload.data())));
        registrar.playToClient(SyncPlayerServerDataS2CPayload.TYPE, SyncPlayerServerDataS2CPayload.CODEC, (payload, ctx) ->
                ctx.enqueueWork(() -> PlayerServerDataClientCache.receive(payload.typeId(), payload.data())));
        registrar.playToServer(AccessibilityPrefsC2SPayload.TYPE, AccessibilityPrefsC2SPayload.CODEC, (payload, ctx) ->
                ctx.enqueueWork(() -> AccessibilityPrefsManager.applyFromClient(
                        ((ServerPlayer) ctx.player()).getUUID(), payload.values())));
        registrar.playToServer(UpdatePlayerClientSettingC2SPayload.TYPE, UpdatePlayerClientSettingC2SPayload.CODEC, (payload, ctx) ->
        {
            ServerPlayer sp = (ServerPlayer) ctx.player();
            ctx.enqueueWork(() -> PlayerClientSettingsManager.applyFromClient(sp, payload.typeId(), payload.data()));
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
        if (NeoForgeClientNetworkAccess.canSend(payload.type()))
        {
            ClientPacketDistributor.sendToServer(payload);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload)
    {
        if (NetworkRegistry.hasChannel(player.connection, payload.type().id()))
        {
            PacketDistributor.sendToPlayer(player, payload);
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
