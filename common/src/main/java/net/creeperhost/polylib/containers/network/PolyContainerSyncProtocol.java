package net.creeperhost.polylib.containers.network;

import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PolyContainerSyncProtocol implements ContainerSyncProtocol {

    private final ModularGuiContainerMenu container;
    private final Map<String, BiConsumer<Player, ?>> clientHandlers = new HashMap<>();
    private final Map<String, BiConsumer<Player, ?>> serverHandlers = new HashMap<>();
    private final Map<String, SyncPayloadType<?>> types = new HashMap<>();

    public PolyContainerSyncProtocol(ModularGuiContainerMenu container) {
        this.container = container;
    }

    @Override
    public <T> void sendToClient(ServerPlayer player, int containerId, SyncPayloadType<T> type, T payload) {
        PolyLibNetwork.sendContainerPacketToClient(player, buf -> {
            buf.writeByte(containerId);
            buf.writeByte(254); // Special ID for protocol routing
            buf.writeUtf(type.id().toString());
            type.codec().encode(buf, payload);
        });
    }

    @Override
    public <T> void sendToServer(int containerId, SyncPayloadType<T> type, T payload) {
        PolyLibNetwork.sendContainerPacketToServer(container.inventory.player.registryAccess(), buf -> {
            buf.writeByte(containerId);
            buf.writeByte(254);
            buf.writeUtf(type.id().toString());
            type.codec().encode(buf, payload);
        });
    }

    @Override
    public <T> void registerClientHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler) {
        clientHandlers.put(type.id().toString(), handler);
        types.put(type.id().toString(), type);
    }

    @Override
    public <T> void registerServerHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler) {
        serverHandlers.put(type.id().toString(), handler);
        types.put(type.id().toString(), type);
    }

    @SuppressWarnings("unchecked")
    public void handleClientPacket(Player player, RegistryFriendlyByteBuf buf) {
        String id = buf.readUtf();
        SyncPayloadType<?> type = types.get(id);
        BiConsumer<Player, Object> handler = (BiConsumer<Player, Object>) clientHandlers.get(id);
        if (type != null && handler != null) {
            Object payload = type.codec().decode(buf);
            handler.accept(player, payload);
        }
    }

    @SuppressWarnings("unchecked")
    public void handleServerPacket(Player player, RegistryFriendlyByteBuf buf) {
        String id = buf.readUtf();
        SyncPayloadType<?> type = types.get(id);
        BiConsumer<Player, Object> handler = (BiConsumer<Player, Object>) serverHandlers.get(id);
        if (type != null && handler != null) {
            Object payload = type.codec().decode(buf);
            handler.accept(player, payload);
        }
    }
}
