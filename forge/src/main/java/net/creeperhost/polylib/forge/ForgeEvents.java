package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.forge.inventory.energy.ForgeEnergyContainer;
import net.creeperhost.polylib.forge.inventory.energy.ForgeItemEnergyContainer;
import net.creeperhost.polylib.forge.inventory.item.ItemContainerWrapper;
import net.creeperhost.polylib.inventory.energy.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.energy.PolyEnergyItem;
import net.creeperhost.polylib.inventory.item.ItemInventoryBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PolyLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents
{
    @SubscribeEvent
    public static void onChunkLoadEvent(ChunkEvent.Load event)
    {
        ChunkEvents.CHUNK_LOAD_EVENT.invoker().onChunkLoad((Level) event.getLevel(), (LevelChunk) event.getChunk());
    }

    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event)
    {
        ChunkEvents.CHUNK_UNLOAD_EVENT.invoker().onChunkUnload((Level) event.getLevel(), (LevelChunk) event.getChunk());
    }

    @SubscribeEvent
    public static void OnAttachCapabilitiesEvent(AttachCapabilitiesEvent<BlockEntity> event)
    {
        if (event.getObject() instanceof PolyEnergyBlock<?> energyBlock)
        {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "energy"), new ForgeEnergyContainer<>(energyBlock.getEnergyStorage(), event.getObject()));
        }
        if(event.getObject() instanceof ItemInventoryBlock itemInventoryBlock)
        {
            String name = event.getObject().getBlockState().getBlock().getDescriptionId();
            PolyLib.LOGGER.log(org.apache.logging.log4j.Level.INFO, "Adding item cap to " + name);
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "item"), new ItemContainerWrapper(itemInventoryBlock.getContainer()));
        }
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (event.getObject().getItem() instanceof PolyEnergyItem<?> energyItem)
        {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "energy"), new ForgeItemEnergyContainer<>(energyItem.getEnergyStorage(event.getObject()), event.getObject()));
        }
    }
}
