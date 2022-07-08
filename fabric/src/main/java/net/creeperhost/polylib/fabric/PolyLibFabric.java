package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ChunkEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class PolyLibFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PolyLib.init();
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> ChunkEvents.CHUNK_LOAD_EVENT.invoker().onChunkLoad(world, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ChunkEvents.CHUNK_UNLOAD_EVENT.invoker().onChunkUnload(world, chunk));
    }
}
