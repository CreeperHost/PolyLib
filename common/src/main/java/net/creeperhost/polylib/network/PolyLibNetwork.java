package net.creeperhost.polylib.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.containers.DataManagerContainer;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.fabricmc.api.EnvType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
 * Created by brandon3055 on 13/01/2024
 */
public class PolyLibNetwork {
    private static final Logger LOGGER = LogManager.getLogger();

    //Server To Client
    private static final ResourceLocation CONTAINER_PACKET_TO_CLIENT = new ResourceLocation(PolyLib.MOD_ID, "container_client");
    private static final ResourceLocation TILE_DATA_VALUE_TO_CLIENT = new ResourceLocation(PolyLib.MOD_ID, "tile_client");

    //Client to server
    private static final ResourceLocation CONTAINER_PACKET_TO_SERVER = new ResourceLocation(PolyLib.MOD_ID, "container_packet_server");
    private static final ResourceLocation TILE_DATA_VALUE_TO_SERVER = new ResourceLocation(PolyLib.MOD_ID, "tile_data_server");
    private static final ResourceLocation TILE_PACKET_TO_SERVER = new ResourceLocation(PolyLib.MOD_ID, "tile_packet_server");

    public static void init() {
        if (Platform.getEnv() == EnvType.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, CONTAINER_PACKET_TO_CLIENT, (buf, context) -> {
                ByteBuf copy = buf.copy();
                context.queue(() -> ModularGuiContainerMenu.handlePacketFromServer(context.getPlayer(), new FriendlyByteBuf(copy)));
            });
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, TILE_DATA_VALUE_TO_CLIENT, (buf, context) -> {
                ByteBuf copy = buf.copy();
                context.queue(() -> handleTileDataValueFromServer(context.getPlayer(), new FriendlyByteBuf(copy)));
            });
        }
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CONTAINER_PACKET_TO_SERVER, (buf, context) -> {
            ByteBuf copy = buf.copy();
            context.queue(() -> ModularGuiContainerMenu.handlePacketFromClient(context.getPlayer(), new FriendlyByteBuf(copy)));
        });
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, TILE_DATA_VALUE_TO_SERVER, (buf, context) -> {
            ByteBuf copy = buf.copy();
            context.queue(() -> handleTileDataValueFromClient(context.getPlayer(), new FriendlyByteBuf(copy)));
        });
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, TILE_PACKET_TO_SERVER, (buf, context) -> {
            ByteBuf copy = buf.copy();
            context.queue(() -> handleTilePacketFromClient(context.getPlayer(), new FriendlyByteBuf(copy)));
        });
    }

    // Client to server messages

    public static void sendContainerPacketToServer(Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToServer(CONTAINER_PACKET_TO_SERVER, buf);
    }

    public static void sendDataValueToServerTile(Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToServer(TILE_DATA_VALUE_TO_SERVER, buf);
    }

    public static void sendPacketToServerTile(Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToServer(TILE_PACKET_TO_SERVER, buf);
    }

    // Server to client messages

    public static void sendContainerPacketToClient(ServerPlayer player, Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToPlayer(player, CONTAINER_PACKET_TO_CLIENT, buf);
    }

    public static void sendTileDataValueToClients(Level level, BlockPos pos, Consumer<FriendlyByteBuf> packetWriter) {
        if (level instanceof ServerLevel serverLevel) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packetWriter.accept(buf);
            List<ServerPlayer> players = serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
            NetworkManager.sendToPlayers(players, TILE_DATA_VALUE_TO_CLIENT, buf);
        }
    }

    // Packet Handling

    private static void handleTileDataValueFromClient(Player player, FriendlyByteBuf packet) {
        try {
            if (player.containerMenu instanceof DataManagerContainer menu && player instanceof ServerPlayer serverPlayer) {
                menu.handleDataValueFromClient(serverPlayer, packet);
            }
        } catch (Throwable e) {
            LOGGER.error("Something went wrong while attempting to read a value packet sent from this client: {}", player, e);
        }
    }

    public static void handleTileDataValueFromServer(Player player, FriendlyByteBuf packet) {
        BlockPos pos = packet.readBlockPos();
        try {
            if (player.level().getBlockEntity(pos) instanceof DataManagerBlock tile) {
                tile.getDataManager().handleSyncFromServer(player, packet);
            }
        } catch (Throwable e) {
            LOGGER.error("Failed to handle tile data from server. Tile pos: {}", pos, e);
        }
    }

    private static void handleTilePacketFromClient(Player player, FriendlyByteBuf packet) {
        try {
            if (player.containerMenu instanceof DataManagerContainer menu && player instanceof ServerPlayer serverPlayer) {
                menu.handlePacketFromClient(serverPlayer, packet);
            }
        } catch (Throwable e) {
            LOGGER.error("Something went wrong while attempting to read a packet sent from this client: {}", player, e);
        }
    }
}
