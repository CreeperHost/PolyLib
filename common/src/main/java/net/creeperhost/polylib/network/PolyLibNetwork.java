package net.creeperhost.polylib.network;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.containers.DataManagerContainer;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.creeperhost.polylib.network.packets.*;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Consumer;

/**
 * Cross-loader networking facade used by PolyLib containers and data managers.
 * <p>
 * Public send methods collect packet contents into a {@link RegistryFriendlyByteBuf},
 * wrap them in a typed PolyLib payload, and delegate transport to the active platform
 * service. Public handle methods route received payloads back to containers or
 * block-entity data managers with guarded error logging.
 */
public class PolyLibNetwork
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Registers common packet handlers with the active platform network service.
     */
    public static void init()
    {
        Services.NETWORK.init();
    }

    /**
     * Registers client-only packet handlers with the active platform network service.
     */
    public static void initClient()
    {
        Services.NETWORK.initClient();
    }

    /**
     * Sends a container packet from the client to the server.
     *
     * @param registryAccess registry access used by the packet buffer
     * @param writer         callback that writes packet contents
     */
    public static void sendContainerPacketToServer(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new ContainerServerPayload(toBytes(registryAccess, writer)));
    }

    /**
     * Sends a data-manager value update from a client container to its server block entity.
     *
     * @param registryAccess registry access used by the packet buffer
     * @param writer         callback that writes packet contents
     */
    public static void sendDataValueToServerTile(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new TileDataServerPayload(toBytes(registryAccess, writer)));
    }

    /**
     * Sends a custom block-entity packet from a client container to the server.
     *
     * @param registryAccess registry access used by the packet buffer
     * @param writer         callback that writes packet contents
     */
    public static void sendPacketToServerTile(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new TilePacketServerPayload(toBytes(registryAccess, writer)));
    }

    /**
     * Sends a container packet from the server to a specific client.
     *
     * @param player the player receiving the packet
     * @param writer callback that writes packet contents
     */
    public static void sendContainerPacketToClient(ServerPlayer player, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToPlayer(player, new ContainerClientPayload(toBytes(player.registryAccess(), writer)));
    }

    /**
     * Sends a block-entity data-manager value update to players watching the chunk containing {@code pos}.
     *
     * @param level  the level containing the block entity
     * @param pos    the block entity position
     * @param writer callback that writes packet contents
     */
    public static void sendTileDataValueToClients(Level level, BlockPos pos, Consumer<RegistryFriendlyByteBuf> writer)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            byte[] data = toBytes(level.registryAccess(), writer);
            List<ServerPlayer> players = serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(pos), false);
            Services.NETWORK.sendToPlayers(players, new TileDataClientPayload(data));
        }
    }

    /**
     * Handles a container packet received from a client.
     *
     * @param player the sending player
     * @param buf    packet data positioned at the container payload
     */
    public static void handleContainerFromClient(Player player, RegistryFriendlyByteBuf buf)
    {
        try
        {
            ModularGuiContainerMenu.handlePacketFromClient(player, buf);
        } catch (Throwable e)
        {
            LOGGER.error("Error handling container packet from client for player {}", player.getName().getString(), e);
        }
    }

    /**
     * Handles a data-manager value update received from a client-side container.
     *
     * @param player the sending player
     * @param buf    packet data positioned at the tile data payload
     */
    public static void handleTileDataFromClient(Player player, RegistryFriendlyByteBuf buf)
    {
        try
        {
            if (player.containerMenu instanceof DataManagerContainer menu && player instanceof ServerPlayer serverPlayer)
            {
                menu.handleDataValueFromClient(serverPlayer, buf);
            }
        } catch (Throwable e)
        {
            LOGGER.error("Error handling tile data packet from client for player {}", player.getName().getString(), e);
        }
    }

    /**
     * Handles a custom block-entity packet received from a client-side container.
     *
     * @param player the sending player
     * @param buf    packet data positioned at the tile packet payload
     */
    public static void handleTilePacketFromClient(Player player, RegistryFriendlyByteBuf buf)
    {
        try
        {
            if (player.containerMenu instanceof DataManagerContainer menu && player instanceof ServerPlayer serverPlayer)
            {
                menu.handlePacketFromClient(serverPlayer, buf);
            }
        } catch (Throwable e)
        {
            LOGGER.error("Error handling tile packet from client for player {}", player.getName().getString(), e);
        }
    }

    /**
     * Handles a container packet received from the server.
     *
     * @param player the client player receiving the packet
     * @param buf    packet data positioned at the container payload
     */
    public static void handleContainerFromServer(Player player, RegistryFriendlyByteBuf buf)
    {
        try
        {
            ModularGuiContainerMenu.handlePacketFromServer(player, buf);
        } catch (Throwable e)
        {
            LOGGER.error("Error handling container packet from server", e);
        }
    }

    /**
     * Handles a block-entity data-manager value update received from the server.
     * <p>
     * The packet must begin with the target block position.
     *
     * @param player the client player receiving the packet
     * @param packet packet data positioned at the block position
     */
    public static void handleTileDataValueFromServer(Player player, RegistryFriendlyByteBuf packet)
    {
        BlockPos pos = packet.readBlockPos();
        try
        {
            if (player.level().getBlockEntity(pos) instanceof DataManagerBlock tile)
            {
                tile.getDataManager().handleSyncFromServer(player, packet);
            }
        } catch (Throwable e)
        {
            LOGGER.error("Failed to handle tile data from server at pos {}", pos, e);
        }
    }

    /**
     * Builds a byte array payload by writing into a registry-aware buffer.
     *
     * @param registryAccess registry access used by the packet buffer
     * @param writer         callback that writes packet contents
     * @return the encoded packet bytes
     */
    private static byte[] toBytes(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        writer.accept(buf);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        return data;
    }
}
