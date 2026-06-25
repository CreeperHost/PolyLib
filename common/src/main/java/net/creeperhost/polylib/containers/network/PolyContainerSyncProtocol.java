package net.creeperhost.polylib.containers.network;

import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Default {@link ContainerSyncProtocol} implementation used by {@link ModularGuiContainerMenu}.
 * <p>
 * This protocol sends typed payloads through the container packet channel using packet id {@code 254}
 * as the routing id. The payload type id is written next, then the payload is encoded with the
 * {@link SyncPayloadType#codec()} registered for that id.
 * <p>
 * Handlers must be registered on the receiving side before packets arrive. Unknown payload ids are
 * ignored rather than decoded.
 */
public class PolyContainerSyncProtocol implements ContainerSyncProtocol {

    private final ModularGuiContainerMenu container;
    private final Map<String, BiConsumer<Player, ?>> clientHandlers = new HashMap<>();
    private final Map<String, BiConsumer<Player, ?>> serverHandlers = new HashMap<>();
    private final Map<String, SyncPayloadType<?>> types = new HashMap<>();

    /**
     * Creates a sync protocol bound to a container menu.
     *
     * @param container the container whose packet channel and player registry access are used
     */
    public PolyContainerSyncProtocol(ModularGuiContainerMenu container) {
        this.container = container;
    }

    /**
     * Sends a typed payload to a client-side container.
     * <p>
     * The payload is routed through the target container id and decoded by a client handler registered
     * with {@link #registerClientHandler(SyncPayloadType, BiConsumer)}.
     *
     * @param player      the server player receiving the payload
     * @param containerId the vanilla container id for the target menu instance
     * @param type        the payload type and codec
     * @param payload     the payload to encode
     * @param <T>         the payload value type
     */
    @Override
    public <T> void sendToClient(ServerPlayer player, int containerId, SyncPayloadType<T> type, T payload) {
        PolyLibNetwork.sendContainerPacketToClient(player, buf -> {
            buf.writeByte(containerId);
            buf.writeByte(254); // Special ID for protocol routing
            buf.writeUtf(type.id().toString());
            type.codec().encode(buf, payload);
        });
    }

    /**
     * Sends a typed payload to the server-side container.
     * <p>
     * The payload is routed through the target container id and decoded by a server handler registered
     * with {@link #registerServerHandler(SyncPayloadType, BiConsumer)}.
     *
     * @param containerId the vanilla container id for the target menu instance
     * @param type        the payload type and codec
     * @param payload     the payload to encode
     * @param <T>         the payload value type
     */
    @Override
    public <T> void sendToServer(int containerId, SyncPayloadType<T> type, T payload) {
        PolyLibNetwork.sendContainerPacketToServer(container.inventory.player.registryAccess(), buf -> {
            buf.writeByte(containerId);
            buf.writeByte(254);
            buf.writeUtf(type.id().toString());
            type.codec().encode(buf, payload);
        });
    }

    /**
     * Registers a client-side handler for payloads sent by the server.
     *
     * @param type    the payload type this handler can decode
     * @param handler the callback receiving the client player and decoded payload
     * @param <T>     the payload value type
     */
    @Override
    public <T> void registerClientHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler) {
        clientHandlers.put(type.id().toString(), handler);
        types.put(type.id().toString(), type);
    }

    /**
     * Registers a server-side handler for payloads sent by the client.
     *
     * @param type    the payload type this handler can decode
     * @param handler the callback receiving the server player and decoded payload
     * @param <T>     the payload value type
     */
    @Override
    public <T> void registerServerHandler(SyncPayloadType<T> type, BiConsumer<Player, T> handler) {
        serverHandlers.put(type.id().toString(), handler);
        types.put(type.id().toString(), type);
    }

    /**
     * Handles a routed payload received by the client-side container.
     * <p>
     * Called by {@link ModularGuiContainerMenu#handlePacketFromServer(Player, int, RegistryFriendlyByteBuf)}
     * after the container id and protocol packet id have already been read.
     *
     * @param player the client player receiving the payload
     * @param buf    the packet data positioned at the payload type id
     */
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

    /**
     * Handles a routed payload received by the server-side container.
     * <p>
     * Called by {@link ModularGuiContainerMenu#handlePacketFromClient(Player, int, RegistryFriendlyByteBuf)}
     * after the container id and protocol packet id have already been read.
     *
     * @param player the player who sent the payload
     * @param buf    the packet data positioned at the payload type id
     */
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
