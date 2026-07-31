package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.network.OptionalPacketHandler;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.network.packets.ContainerClientPayload;
import net.creeperhost.polylib.network.packets.TileDataClientPayload;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataClientCache;
import net.creeperhost.polylib.player.serverdata.SyncPlayerServerDataS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingSyncS2CPayload;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsClientCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Owns Fabric networking calls that are only valid on a physical client.
 *
 * <p>Keeping these references out of {@link FabricNetworkHelper} allows that
 * service to be constructed safely by a dedicated server.</p>
 */
@Environment(EnvType.CLIENT)
final class FabricClientNetwork
{
    private FabricClientNetwork() {}

    /**
     * Registers PolyLib's built-in clientbound packet handlers.
     */
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

    /**
     * Registers a clientbound optional packet handler.
     *
     * @param type payload type
     * @param handler handler invoked when the payload arrives
     * @param <T> payload type
     */
    static <T extends CustomPacketPayload> void registerOptionalHandler(
            CustomPacketPayload.Type<T> type,
            OptionalPacketHandler<T> handler)
    {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                handler.handle(payload, context.player()));
    }

    /**
     * Checks whether the connected server accepts a payload type.
     *
     * @param type payload type
     * @return {@code true} when the server accepts the payload
     */
    static boolean canSend(CustomPacketPayload.Type<?> type)
    {
        return ClientPlayNetworking.canSend(type);
    }

    /**
     * Sends a payload to the connected server.
     *
     * @param payload payload to send
     */
    static void send(CustomPacketPayload payload)
    {
        ClientPlayNetworking.send(payload);
    }
}
