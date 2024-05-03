package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.creeperhost.polylib.neoforge.inventory.fluid.PolyNeoFluidWrapper;
import net.creeperhost.polylib.neoforge.inventory.power.PolyNeoEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@Mod(PolyLib.MOD_ID)
public class PolyLibNeoForge
{
    public PolyLibNeoForge(IEventBus modEventBus)
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> NeoForgeClientEvents::init);

        PolyLib.init();
        NeoForgeEvents.init();

        modEventBus.addListener(EventPriority.LOWEST, this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        PolyLib.LOGGER.info("=====Registering Capabilities=====");

        for (Item item : BuiltInRegistries.ITEM)
        {
            if(item instanceof PolyEnergyItem polyEnergyItem)
            {
                PolyLib.LOGGER.info("Adding EnergyStore Item to " + item.getDescription().getString());

                event.registerItem(Capabilities.EnergyStorage.ITEM, (object, object2) -> new PolyNeoEnergyWrapper(polyEnergyItem.getEnergyStorage(object)), item);
            }
        }

        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE)
        {
            try
            {
                //This is terrible... There has to be a better way!
                BlockEntity blockEntity = blockEntityType.create(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
                if (blockEntity == null) continue;
                if (blockEntity instanceof PolyInventoryBlock invBlock)
                {
                    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType, (entity, side) -> new InvWrapper(invBlock.getContainer(side)));
                }
                if (blockEntity instanceof PolyEnergyBlock) {
                    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, blockEntityType, (entity, side) -> new PolyNeoEnergyWrapper(((PolyEnergyBlock)entity).getEnergyStorage(side)));
                }
                if (blockEntity instanceof PolyFluidBlock) {
                    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, blockEntityType, (entity, side) -> new PolyNeoFluidWrapper(((PolyFluidBlock)entity).getFluidHandler(side)));
                }
            } catch (Exception ignored) {}
        }
    }
}
