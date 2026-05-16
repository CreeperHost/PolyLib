package net.creeperhost.polylib.chunkmap.server;

import com.google.common.collect.Iterables;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.chunkmap.common.network.*;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ChunkMapSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PACKET_PARTITION = 20_000;

    private final ServerPlayer player;
    private final Set<ResourceKey<Level>> watchingDimensions = Collections.synchronizedSet(new HashSet<>());
    private boolean isPermitted;

    public ChunkMapSession(ServerPlayer player) {
        this.player = player;
        this.isPermitted = PolyChunkMapServer.getInstance().isPermitted(player);
    }

    public java.util.UUID getPlayerId() {
        return player.getUUID();
    }

    public void checkPermissions() {
        boolean nowPermitted = PolyChunkMapServer.getInstance().isPermitted(player);
        if (this.isPermitted != nowPermitted) {
            this.isPermitted = nowPermitted;
            if (nowPermitted) {
                player.connection.send(new ClientboundCustomPayloadPacket(PolyChunkMapHelloPayload.INSTANCE));
            } else {
                watchingDimensions.clear();
                player.connection.send(new ClientboundCustomPayloadPacket(PolyChunkMapByePayload.INSTANCE));
            }
        } else if (nowPermitted) {
            // Already permitted but permissions were updated (e.g. op event fired again)
            player.connection.send(new ClientboundCustomPayloadPacket(PolyChunkMapHelloPayload.INSTANCE));
        }
    }

    public void onPlayerJoin() {
        if (isPermitted) {
            player.connection.send(new ClientboundCustomPayloadPacket(PolyChunkMapHelloPayload.INSTANCE));
        }
    }

    public void startWatching(List<ResourceKey<Level>> dimensions) {
        if (!isPermitted) {
            LOGGER.warn("Player {} attempted chunk-map without permission", player.getScoreboardName());
            return;
        }
        MinecraftServer server = ((ServerLevel) player.level()).getServer();
        if (server == null) return;
        int tick = server.getTickCount();

        for (ResourceKey<Level> dimension : dimensions) {
            ServerLevel level = server.getLevel(dimension);
            if (level == null) {
                LOGGER.warn("Player {} requested unknown dimension {}", player.getScoreboardName(), dimension);
                continue;
            }
            if (watchingDimensions.add(dimension)) {
                Collection<PolyChunkMapData> all = ((PolyChunkTrackerHolder) level).polylib$getChunkTracker().getAll();
                partitionInto(all, partition -> {
                    PolyChunkMapDataPayload p = new PolyChunkMapDataPayload(dimension, partition, tick, true);
                    player.connection.send(new ClientboundCustomPayloadPacket(p));
                });
            }
        }
    }

    public void stopWatching(List<ResourceKey<Level>> dimensions) {
        if (dimensions.isEmpty()) {
            watchingDimensions.clear();
        } else {
            dimensions.forEach(watchingDimensions::remove);
        }
    }
    
    public void onLevelUnload(ResourceKey<Level> dimension) {
        watchingDimensions.remove(dimension);
    }

    public void sendTickUpdates(ServerLevel level, PolyChunkTracker.DirtyChunks dirty) {
        if (!isPermitted || !watchingDimensions.contains(level.dimension())) return;

        partitionInto(dirty.updated(), partition -> {
            PolyChunkMapDataPayload payload = new PolyChunkMapDataPayload(
                    level.dimension(), partition, level.getServer().getTickCount(), false);
            player.connection.send(new ClientboundCustomPayloadPacket(payload));
        });

        if (!dirty.removed().isEmpty()) {
            PolyChunkMapUnloadPayload payload = new PolyChunkMapUnloadPayload(
                    level.dimension(), dirty.removed().toLongArray());
            player.connection.send(new ClientboundCustomPayloadPacket(payload));
        }
    }

    private <T> void partitionInto(Collection<T> data, Consumer<Collection<T>> consumer) {
        if (data.isEmpty()) return;
        if (data.size() <= PACKET_PARTITION) { consumer.accept(data); return; }
        for (Collection<T> partition : Iterables.partition(data, PACKET_PARTITION)) {
            consumer.accept(partition);
        }
    }
}
