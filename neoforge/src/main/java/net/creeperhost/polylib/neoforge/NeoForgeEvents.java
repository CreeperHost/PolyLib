package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.events.ChunkEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

public class NeoForgeEvents {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(NeoForgeEvents::onChunkLoadEvent);
        NeoForge.EVENT_BUS.addListener(NeoForgeEvents::onChunkUnloadEvent);
    }

    private static void onChunkLoadEvent(ChunkEvent.Load event) {
        ChunkEvents.CHUNK_LOAD_EVENT.invoker().onChunkLoad((Level) event.getLevel(), (LevelChunk) event.getChunk());
    }

    private static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        ChunkEvents.CHUNK_UNLOAD_EVENT.invoker().onChunkUnload((Level) event.getLevel(), (LevelChunk) event.getChunk());
    }
}
