package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class PolyLibFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PolyLib.init();
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> ChunkEvents.CHUNK_LOAD_EVENT.invoker().onChunkLoad(world, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ChunkEvents.CHUNK_UNLOAD_EVENT.invoker().onChunkUnload(world, chunk));
        if(Platform.getEnv() == EnvType.CLIENT)
        {
            WorldRenderEvents.LAST.register(context -> ClientRenderEvents.LAST.invoker().onRenderLastEvent(context.matrixStack()));
        }
    }
}
