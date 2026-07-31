package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsC2SPayload;
import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.network.OptionalPacketHandler;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.*;
import net.creeperhost.polylib.platform.services.INetworkHelper;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataClientCache;
import net.creeperhost.polylib.player.serverdata.SyncPlayerServerDataS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsClientCache;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingSyncS2CPayload;
import net.creeperhost.polylib.player.settings.UpdatePlayerClientSettingC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NeoForgeNetworkHelper implements INetworkHelper
{
    private static final Map<Identifier, OptionalPayloadRegistration<?>> OPTIONAL_PAYLOADS = new LinkedHashMap<>();
    private static final Map<Identifier, OptionalPacketHandler<?>> OPTIONAL_CLIENT_HANDLERS = new LinkedHashMap<>();
    private static boolean optionalPayloadRegistrationClosed;
    private static boolean optionalClientHandlerRegistrationClosed;

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

        PayloadRegistrar optionalRegistrar = registrar.optional();
        OPTIONAL_PAYLOADS.values().forEach(registration -> registerOptionalPayload(optionalRegistrar, registration));
        optionalPayloadRegistrationClosed = true;
    }

    public static void onRegisterClientPayloadHandlers(RegisterClientPayloadHandlersEvent event)
    {
        OPTIONAL_CLIENT_HANDLERS.forEach((id, handler) ->
        {
            OptionalPayloadRegistration<?> registration = OPTIONAL_PAYLOADS.get(id);
            if (registration == null || !registration.clientbound)
            {
                throw new IllegalStateException("Client handler registered for unknown optional clientbound payload: " + id);
            }
            registerOptionalClientHandler(event, registration, handler);
        });
        optionalClientHandlerRegistrationClosed = true;
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
    public synchronized <T extends CustomPacketPayload> void registerOptionalClientbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        ensureOptionalPayloadRegistrationOpen(type);
        OptionalPayloadRegistration<T> registration = getOrCreateOptionalRegistration(type, codec);
        if (registration.clientbound)
        {
            throw new IllegalStateException("Optional clientbound payload already registered: " + type.id());
        }
        registration.clientbound = true;
    }

    @Override
    public synchronized <T extends CustomPacketPayload> void registerOptionalServerbound(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            OptionalPacketHandler<T> handler)
    {
        ensureOptionalPayloadRegistrationOpen(type);
        OptionalPayloadRegistration<T> registration = getOrCreateOptionalRegistration(type, codec);
        if (registration.serverHandler != null)
        {
            throw new IllegalStateException("Optional serverbound payload already registered: " + type.id());
        }
        registration.serverHandler = handler;
    }

    @Override
    public synchronized <T extends CustomPacketPayload> void registerOptionalClientHandler(
            CustomPacketPayload.Type<T> type,
            OptionalPacketHandler<T> handler)
    {
        if (optionalClientHandlerRegistrationClosed)
        {
            throw new IllegalStateException("Optional client handler registered too late: " + type.id());
        }
        if (OPTIONAL_CLIENT_HANDLERS.putIfAbsent(type.id(), handler) != null)
        {
            throw new IllegalStateException("Optional client handler already registered: " + type.id());
        }
    }

    @Override
    public boolean canSendOptionalToServer(CustomPacketPayload.Type<?> type)
    {
        var listener = Minecraft.getInstance().getConnection();
        return listener != null && NetworkRegistry.hasChannel(listener, type.id());
    }

    @Override
    public boolean canSendOptionalToPlayer(ServerPlayer player, CustomPacketPayload.Type<?> type)
    {
        return NetworkRegistry.hasChannel(player.connection, type.id());
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

    private static void ensureOptionalPayloadRegistrationOpen(CustomPacketPayload.Type<?> type)
    {
        if (optionalPayloadRegistrationClosed)
        {
            throw new IllegalStateException("Optional payload registered too late: " + type.id());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> OptionalPayloadRegistration<T> getOrCreateOptionalRegistration(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        OptionalPayloadRegistration<?> existing = OPTIONAL_PAYLOADS.get(type.id());
        if (existing != null)
        {
            return (OptionalPayloadRegistration<T>) existing;
        }
        OptionalPayloadRegistration<T> registration = new OptionalPayloadRegistration<>(type, codec);
        OPTIONAL_PAYLOADS.put(type.id(), registration);
        return registration;
    }

    private static <T extends CustomPacketPayload> void registerOptionalPayload(
            PayloadRegistrar registrar,
            OptionalPayloadRegistration<T> registration)
    {
        if (registration.clientbound && registration.serverHandler != null)
        {
            registrar.playBidirectional(registration.type, registration.codec,
                    (payload, context) -> registration.serverHandler.handle(payload, context.player()));
        }
        else if (registration.clientbound)
        {
            registrar.playToClient(registration.type, registration.codec);
        }
        else if (registration.serverHandler != null)
        {
            registrar.playToServer(registration.type, registration.codec,
                    (payload, context) -> registration.serverHandler.handle(payload, context.player()));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerOptionalClientHandler(
            RegisterClientPayloadHandlersEvent event,
            OptionalPayloadRegistration<?> registration,
            OptionalPacketHandler<?> handler)
    {
        OptionalPayloadRegistration<T> typedRegistration = (OptionalPayloadRegistration<T>) registration;
        OptionalPacketHandler<T> typedHandler = (OptionalPacketHandler<T>) handler;
        event.register(typedRegistration.type, (payload, context) ->
                typedHandler.handle(payload, context.player()));
    }

    private static final class OptionalPayloadRegistration<T extends CustomPacketPayload>
    {
        private final CustomPacketPayload.Type<T> type;
        private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
        private boolean clientbound;
        private OptionalPacketHandler<T> serverHandler;

        private OptionalPayloadRegistration(
                CustomPacketPayload.Type<T> type,
                StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
        {
            this.type = type;
            this.codec = codec;
        }
    }

}
