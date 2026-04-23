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

public class PolyLibNetwork
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        Services.NETWORK.init();
    }

    public static void initClient()
    {
        Services.NETWORK.initClient();
    }

    public static void sendContainerPacketToServer(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new ContainerServerPayload(toBytes(registryAccess, writer)));
    }

    public static void sendDataValueToServerTile(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new TileDataServerPayload(toBytes(registryAccess, writer)));
    }

    public static void sendPacketToServerTile(RegistryAccess registryAccess, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToServer(new TilePacketServerPayload(toBytes(registryAccess, writer)));
    }

    public static void sendContainerPacketToClient(ServerPlayer player, Consumer<RegistryFriendlyByteBuf> writer)
    {
        Services.NETWORK.sendToPlayer(player, new ContainerClientPayload(toBytes(player.registryAccess(), writer)));
    }

    public static void sendTileDataValueToClients(Level level, BlockPos pos, Consumer<RegistryFriendlyByteBuf> writer)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            byte[] data = toBytes(level.registryAccess(), writer);
            List<ServerPlayer> players = serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(pos), false);
            Services.NETWORK.sendToPlayers(players, new TileDataClientPayload(data));
        }
    }

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
