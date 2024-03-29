package net.creeperhost.polylib.client.modulargui.lib.container;

import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class provides a convenient way to synchronize server side data with a client side screen via the ContainerMenu
 * <p>
 * Created by brandon3055 on 09/09/2023
 */
public class DataSync<T> {

    private final ModularGuiContainerMenu containerMenu;
    private final AbstractDataStore<T> dataStore;
    private final Supplier<T> valueGetter;

    public DataSync(ModularGuiContainerMenu containerMenu, AbstractDataStore<T> dataStore, Supplier<T> valueGetter) {
        this.containerMenu = containerMenu;
        this.dataStore = dataStore;
        this.valueGetter = valueGetter;
        containerMenu.dataSyncs.add(this);
    }

    public T get() {
        return dataStore.get();
    }

    /**
     * This should only ever be called server side!
     */
    public void detectAndSend() {
        if (dataStore.isSameValue(valueGetter.get())) {
            return;
        }
        dataStore.set(valueGetter.get());
        containerMenu.sendPacketToClient(255, buf -> {
            buf.writeByte((byte) containerMenu.dataSyncs.indexOf(this));
            dataStore.toBytes(buf);
        });
    }

    public void handleSyncPacket(FriendlyByteBuf buf) {
        dataStore.fromBytes(buf);
    }
}
