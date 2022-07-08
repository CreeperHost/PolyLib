package net.creeperhost.polylib.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkEvents
{
    Event<ChunkEvents.ChunkLoadEvent> CHUNK_LOAD_EVENT = EventFactory.createEventResult();
    Event<ChunkEvents.ChunkUnloadEvent> CHUNK_UNLOAD_EVENT = EventFactory.createEventResult();

    interface ChunkLoadEvent
    {
        void onChunkLoad(Level level, LevelChunk chunk);
    }

    interface ChunkUnloadEvent
    {
        void onChunkUnload(Level level, LevelChunk chunk);
    }

}
