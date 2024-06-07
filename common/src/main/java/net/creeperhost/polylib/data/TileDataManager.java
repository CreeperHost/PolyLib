package net.creeperhost.polylib.data;

import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.creeperhost.polylib.data.DataManagerBlock.*;

/**
 * This class provides a way to save and synchronize tile data with the client.
 * As well as the ability to set fields in your tile via a client side container screen.
 * <p>
 * To get started simply define a TileDataManager instance in your tile and call the {@link #tick()}, {@link #load(CompoundTag)} and {@link #save(CompoundTag)}
 * methods from the appropriate methods on your tile.
 * <p>
 * You can then define a data field in you tile like so:
 * public IntData myIntField = dataManager.register("int_field_name", new IntData(0), SAVE, SYNC, CLIENT_CONTROL);
 * <p>
 * The SAVE, SYNC and CLIENT_CONTROL flags are each optional,
 * Take a look at #register(String, AbstractDataStore, int...) for more info.
 * <p>
 * Created by brandon3055 on 13/01/2024
 */
public class TileDataManager<BE extends BlockEntity & DataManagerBlock> {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected final BE tile;
    protected Map<String, AbstractDataStore<?>> dataStoreMap = new HashMap<>();
    protected Map<AbstractDataStore<?>, Integer> dataFlags = new HashMap<>();
    protected List<AbstractDataStore<?>> dataOrder = new ArrayList<>();

    public TileDataManager(BE tile) {
        this.tile = tile;
    }

    /**
     * Register a data storage object to be managed by this data manager.
     *
     * @param name  A unique name that will be used when writing this storage to NBT.
     * @param data  The data storage instance.
     * @param flags Storage flags, Valid flags are defined in {@link DataManagerBlock}
     *              Current flags are:
     *              {@link DataManagerBlock#SAVE} - Enables saving to NBT
     *              {@link DataManagerBlock#SYNC} - Enables synchronization to client
     *              {@link DataManagerBlock#CLIENT_CONTROL} - Allows this value to be set from a client side container screen.
     */
    public <D extends AbstractDataStore<?>> D register(String name, D data, int... flags) {
        assert ResourceLocation.isValidResourceLocation(name);
        int combinedFlags = 0;
        for (int flag : flags) {
            combinedFlags |= flag;
        }
        dataStoreMap.put(name, data);
        dataFlags.put(data, combinedFlags);
        dataOrder.add(data);
        return data;
    }

    //## Call these methods from the appropriate functions in your BlockEntity ##

    /**
     * Call from tiles tick method.
     */
    public void tick() {
        if (tile.getLevel().isClientSide()) return;
        boolean changes = false;
        for (AbstractDataStore<?> data : dataOrder) {
            if (data.isDirty(true)) {
                sendToClient(data);
                changes = true;
            }
        }
        if (changes) {
            tile.setChanged();
        }
    }

    /**
     * Call from tiles load method.
     */
    public void load(CompoundTag tag) {
        dataStoreMap.forEach((name, data) -> {
            if ((dataFlags.get(data) & SAVE) > 0 && tag.contains(name)) {
                data.fromTag(tag.get(name));
            }
        });
    }

    /**
     * Call from tiles save method.
     */
    public void save(CompoundTag tag) {
        dataStoreMap.forEach((name, data) -> {
            if ((dataFlags.get(data) & SAVE) > 0) {
                tag.put(name, data.toTag());
            }
        });
    }

    public void loadFromItem(CompoundTag tag) {
        dataStoreMap.forEach((name, data) -> {
            if ((dataFlags.get(data) & SAVE_TO_ITEM) > 0 && tag.contains(name)) {
                data.fromTag(tag.get(name));
            }
        });
    }

    public void saveToItem(CompoundTag tag) {
        dataStoreMap.forEach((name, data) -> {
            if ((dataFlags.get(data) & SAVE_TO_ITEM) > 0) {
                tag.put(name, data.toTag());
            }
        });
    }

    //### Internal Functions ###

    protected void sendToClient(AbstractDataStore<?> data) {
        if ((dataFlags.get(data) & SYNC) == 0) return;
        int index = dataOrder.indexOf(data);
        BlockPos pos = tile.getBlockPos();
        PolyLibNetwork.sendTileDataValueToClients(tile.getLevel(), pos, buf -> {
            buf.writeBlockPos(pos);
            buf.writeVarInt(index);
            data.toBytes(buf);
        });
    }

    public void handleSyncFromServer(Player player, FriendlyByteBuf packet) {
        int index = packet.readVarInt();
        dataOrder.get(index).fromBytes(packet);
    }

    public void handleSyncFromClient(ServerPlayer player, FriendlyByteBuf packet) {
        int index = packet.readVarInt();
        AbstractDataStore<?> data = dataOrder.get(index);
        if ((dataFlags.get(data) & CLIENT_CONTROL) > 0) {
            data.fromBytes(packet);
            data.markDirty();
        }
    }
}
