package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.polylib.fabric.client.ResourceReloadListenerWrapper;
import net.creeperhost.polylib.fabric.inventory.energy.FabricBlockEnergyContainer;
import net.creeperhost.polylib.fabric.inventory.energy.FabricItemEnergyContainer;
import net.creeperhost.polylib.fabric.inventory.fluid.PolyFabricFluidWrapper;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyItemWrapper;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyWrapper;
import net.creeperhost.polylib.inventory.energy.PolyEnergyContainer;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.fabricmc.api.EnvType;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.creeperhost.polylib.inventory.item.ItemInventoryBlock;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

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

            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListenerWrapper(PolyTextures::getAtlasHolder, new ResourceLocation(PolyLib.MOD_ID, "gui_atlas_reload")));
        }

        //TODO Deprecated
        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof net.creeperhost.polylib.inventory.energy.PolyEnergyBlock<?> attachment) {
                PolyEnergyContainer container = attachment.getEnergyStorage().getContainer(context);
                return container == null ? null : new FabricBlockEnergyContainer(container, attachment.getEnergyStorage(), blockEntity);
            }
            return null;
        });
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof net.creeperhost.polylib.inventory.energy.PolyEnergyItem<?> attachment) {
                return new FabricItemEnergyContainer(context, attachment.getEnergyStorage(itemStack));
            }
            return null;
        });
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof PolyEnergyItem item) {
                if (item.getEnergyStorage(itemStack) instanceof IPolyEnergyStorageItem storage){
                    return new PolyFabricEnergyItemWrapper(storage, context);
                }
            }
            return null;
        });

        FluidStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
            if (blockEntity instanceof PolyFluidBlock fluidBlock) {
                PolyFluidHandler handler = fluidBlock.getFluidHandler(direction);
                if (handler == null) {
                    return null;
                }
                if (handler.getTanks() == 1) {
                    return new PolyFabricFluidWrapper(handler, 0);
                }
                List<PolyFabricFluidWrapper> tanks = new ArrayList<>();
                for (int i = 0; i < handler.getTanks(); i++) {
                    tanks.add(new PolyFabricFluidWrapper(handler, i));
                }
                return new CombinedStorage<>(tanks);
            }
            return null;
        });

        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof ItemInventoryBlock invBlock) {
                return InventoryStorageImpl.of(invBlock.getContainer(context), context);
            }
            else if (blockEntity instanceof PolyInventoryBlock invBlock) {
                return InventoryStorageImpl.of(invBlock.getContainer(context), context);
            }
            return null;
        });

        EnergyStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyEnergyBlock energyBlock) {
                IPolyEnergyStorage storage = energyBlock.getEnergyStorage(context);
                return storage == null ? null : new PolyFabricEnergyWrapper(storage);
            }
            return null;
        });
    }
}
