package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Client-side chunk lifecycle events.
 */
public final class PolyClientChunkEvents
{
    /**
     * Fired when a chunk is loaded into the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientChunkLoadNF} on {@code ClientLevel#onChunkLoaded(ChunkPos)}.<br>
     * Fabric: {@code ClientChunkEvents.CHUNK_LOAD}.
     */
    public static final PolyEvent<ChunkLoad> CLIENT_CHUNK_LOAD = PolyEvent.create(
            handlers -> (level, chunk) -> handlers.forEach(h -> h.onChunkLoad(level, chunk)));

    /**
     * Fired when a chunk is unloaded from the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientChunkUnloadNF} on {@code ClientLevel#unload(LevelChunk)}.<br>
     * Fabric: {@code ClientChunkEvents.CHUNK_UNLOAD}.
     */
    public static final PolyEvent<ChunkUnload> CLIENT_CHUNK_UNLOAD = PolyEvent.create(
            handlers -> (level, chunk) -> handlers.forEach(h -> h.onChunkUnload(level, chunk)));

    private PolyClientChunkEvents() {}

    /**
     * Callback fired when a chunk loads on the client.
     */
    @FunctionalInterface
    public interface ChunkLoad
    {
        void onChunkLoad(ClientLevel level, LevelChunk chunk);
    }

    /**
     * Callback fired when a chunk unloads on the client.
     */
    @FunctionalInterface
    public interface ChunkUnload
    {
        void onChunkUnload(ClientLevel level, LevelChunk chunk);
    }
}
