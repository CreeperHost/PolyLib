package net.creeperhost.polylib.data;

import net.creeperhost.polylib.PolyLibClient;
import net.creeperhost.polylib.containers.DataManagerContainer;
import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Consumer;

/**
 * This interface must be implemented on any BlockEntity that wishes to use the {@link TileDataManager} system.
 * <p>
 * Created by brandon3055 on 13/01/2024
 *
 * @see DataManagerContainer
 */
public interface DataManagerBlock {

    /**
     * Save data value to tile NBT
     */
    int SAVE = 0b1000;
    /**
     * Sync data value to client side tile.
     */
    int SYNC = 0b0100;
    /**
     * Allows data storage value to be set via a client side container screen.
     * For this to work the {@link AbstractContainerMenu} implementation must implement {@link DataManagerContainer}
     */
    int CLIENT_CONTROL = 0b0010;

    /**
     * Save this data to item when the block is harvested.
     */
    int SAVE_TO_ITEM = 0b0001;

    /**
     * Save data value to tile NBT and to item when block is harvested.
     */
    int SAVE_BOTH = SAVE | SAVE_TO_ITEM;

    TileDataManager<?> getDataManager();

    /**
     * Allows you to send a packet from the client side container screen to the server side tile.
     * This packet can contain any data you like.
     * <p>
     * But for this to work the {@link AbstractContainerMenu} implementation must implement {@link DataManagerContainer}
     *
     * @param id           allows you to provide any integer value as a way of identifying this message.
     * @param packetWriter A ByteBuf consumer that you can use to write your data to the packet.
     */
    default void sendPacketToServer(int id, Consumer<FriendlyByteBuf> packetWriter) {
        TileDataManager<?> manager = getDataManager();
        if (!manager.tile.getLevel().isClientSide()) return;
        Player player = PolyLibClient.getClientPlayer();
        if (player == null) return;
        AbstractContainerMenu container = player.containerMenu;
        PolyLibNetwork.sendPacketToServerTile(player.registryAccess(), buf -> {
            buf.writeVarInt(container.containerId);
            buf.writeVarInt(id);
            packetWriter.accept(buf);
        });
    }

    /**
     * Allows you to set the value of any {@link AbstractDataStore} that is managed by this tiles {@link TileDataManager}
     * from the client side container screen.
     * <p>
     * But for this to work the {@link AbstractContainerMenu} implementation must implement {@link DataManagerContainer}
     * And the data store must be registered with the CLIENT_CONTROL flag.
     *
     * @param data  The data store whose value is to be set.
     * @param value The new value.
     */
    default <T> void sendDataValueToServer(AbstractDataStore<T> data, T value) {
        TileDataManager<?> manager = getDataManager();
        if (!manager.tile.getLevel().isClientSide()) return;
        Player player = PolyLibClient.getClientPlayer();
        if (player == null) return;

        int index = manager.dataOrder.indexOf(data);
        PolyLibNetwork.sendDataValueToServerTile(player.registryAccess(), buf -> {
            buf.writeVarInt(player.containerMenu.containerId);
            T prev = data.get();
            data.set(value);
            buf.writeVarInt(index);
            data.toBytes(buf);
            data.set(prev);
            data.isDirty(true);
        });
    }

    /**
     * If you wish to use {@link #sendPacketToServer(int, Consumer)} to sent messages to the server tile,
     * Then you must override this in your tile in order to receive those messages.
     *
     * @param player The player that sent the packet.
     * @param id     The packet id as specified in sendPacketToServer.
     * @param buf    The packet data that was written in sendPacketToServer.
     */
    default void handlePacketFromClient(ServerPlayer player, int id, FriendlyByteBuf buf) {
    }
}
