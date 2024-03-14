package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ChunkEvents;
import net.creeperhost.polylib.forge.inventory.energy.NeoForgeEnergyContainer;
import net.creeperhost.polylib.forge.inventory.energy.NeoForgeItemEnergyContainer;
import net.creeperhost.polylib.forge.inventory.fluid.FluidCapProvider;
import net.creeperhost.polylib.forge.inventory.item.ItemContainerWrapper;
import net.creeperhost.polylib.forge.inventory.power.EnergyCapProvider;
import net.creeperhost.polylib.inventory.energy.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.energy.PolyEnergyItem;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.item.ItemInventoryBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

@Mod.EventBusSubscriber(modid = PolyLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeEvents
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
        if (event.getObject() instanceof net.creeperhost.polylib.inventory.energy.PolyEnergyBlock<?> energyBlock)
        {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "energy"), new NeoForgeEnergyContainer<>(energyBlock.getEnergyStorage(), event.getObject()));
        }
        if(event.getObject() instanceof ItemInventoryBlock itemInventoryBlock)
        {
            String name = event.getObject().getBlockState().getBlock().getDescriptionId();
            PolyLib.LOGGER.log(org.apache.logging.log4j.Level.INFO, "Adding item cap to " + name);
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "item"), new ItemContainerWrapper(itemInventoryBlock.getContainer()));
        }
        if (event.getObject() instanceof PolyFluidBlock fluidBlock) {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "fluid"), new FluidCapProvider(fluidBlock));
        }
        if (event.getObject() instanceof net.creeperhost.polylib.inventory.power.PolyEnergyBlock energyBlock) {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "power"), new EnergyCapProvider(energyBlock));
        }
    }

    @SubscribeEvent
    public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof net.creeperhost.polylib.inventory.energy.PolyEnergyItem<?> energyItem) {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "energy"), new NeoForgeItemEnergyContainer<>(energyItem.getEnergyStorage(stack), stack));
        }
        if (stack.getItem() instanceof net.creeperhost.polylib.inventory.power.PolyEnergyItem energyItem) {
            event.addCapability(new ResourceLocation(PolyLib.MOD_ID, "power"), new EnergyCapProvider(energyItem, stack));
        }
    }
}
