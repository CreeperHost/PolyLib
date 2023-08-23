package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.sprite.GuiTextures;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.polylib.fabric.client.ResourceReloadListenerWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PolyLibFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PolyLib.init();

        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> ChunkEvents.CHUNK_LOAD_EVENT.invoker().onChunkLoad(world, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> ChunkEvents.CHUNK_UNLOAD_EVENT.invoker().onChunkUnload(world, chunk));

        if (Platform.getEnv() == EnvType.CLIENT)
        {
            WorldRenderEvents.END.register(context -> ClientRenderEvents.LAST.invoker().onRenderLastEvent(context.matrixStack()));

            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListenerWrapper(GuiTextures::getAtlasHolder, new ResourceLocation(PolyLib.MOD_ID, "gui_atlas_reload")));
        }

        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyEnergyBlock<?> attachment) {
                PolyEnergyContainer container = attachment.getEnergyStorage().getContainer(context);
                return container == null ? null : new FabricBlockEnergyContainer(container, attachment.getEnergyStorage(), blockEntity);
            }
            return null;
        });
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof PolyEnergyItem<?> attachment) {
                return new FabricItemEnergyContainer(context, attachment.getEnergyStorage(itemStack));
            }
            return null;
        });
    }
}
