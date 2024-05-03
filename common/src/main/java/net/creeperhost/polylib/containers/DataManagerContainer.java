package net.creeperhost.polylib.containers;

import net.creeperhost.polylib.data.DataManagerBlock;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * If you wish to send messages from a client gui to the server via {@link DataManagerBlock}, Then the gui's container must implement this interface.
 * <p>
 * Created by brandon3055 on 14/01/2024
 */
public interface DataManagerContainer {

    <T extends BlockEntity & DataManagerBlock> T getBlockEntity();

    /**
     * This is the handler that is used to route packets from the client, through the container, to the server side block entity.
     */
    default void handlePacketFromClient(ServerPlayer player, RegistryFriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        if (containerId != ((AbstractContainerMenu) this).containerId) return;
        int packetID = buf.readVarInt();
        getBlockEntity().handlePacketFromClient(player, packetID, buf);
    }

    /**
     * Serves the same function as handleClientPacket, except this is used to send client side data values to the server side block entity.
     */
    default void handleDataValueFromClient(ServerPlayer player, RegistryFriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        if (containerId != ((AbstractContainerMenu) this).containerId) return;
        getBlockEntity().getDataManager().handleSyncFromClient(player, buf);
    }
}
