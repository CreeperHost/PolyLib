package net.creeperhost.polylib.neoforge;

import com.google.common.collect.Iterables;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.creeperhost.polylib.neoforge.inventory.fluid.PolyNeoFluidWrapper;
import net.creeperhost.polylib.neoforge.inventory.power.PolyNeoEnergyItemWrapper;
import net.creeperhost.polylib.neoforge.inventory.power.PolyNeoEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

@Mod(PolyLib.MOD_ID)
public class PolyLibNeoForge
{
    public PolyLibNeoForge(IEventBus modEventBus)
    {
        if(Platform.getEnvironment() == Env.CLIENT)
        {
            NeoForgeClientEvents.init(modEventBus);
        }

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
                PolyLib.LOGGER.info("Adding EnergyStore Item to " + item.getName().getString());

//                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new DelegatingEnergyHandler(), item);
//                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new ItemAccessEnergyHandler(), item);
//                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new LimitingEnergyHandler(), item);
//                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new SimpleEnergyHandler(), item);

                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new PolyNeoEnergyItemWrapper(polyEnergyItem.getEnergyStorage(stack), itemAccess), item);
            }
        }

        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE)
        {
            try
            {
                Identifier reg = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
                ModContainer mod = ModList.get().getModContainerById(reg.getNamespace()).orElse(null);
                if (mod == null || mod.getModInfo().getDependencies().stream().noneMatch(e -> e.getModId().equals(PolyLib.MOD_ID))) {
                    continue;
                }

                //This is terrible... There has to be a better way!
                Block block = Iterables.getFirst(blockEntityType.getValidBlocks(), null);
                if (block == null) {
                    continue;
                }
                BlockEntity blockEntity = blockEntityType.create(BlockPos.ZERO, block.defaultBlockState());
                if (blockEntity == null) continue;
                if (blockEntity instanceof PolyInventoryBlock) {
                    event.registerBlockEntity(Capabilities.Item.BLOCK, blockEntityType, (entity, side) -> getInvWrapper(((PolyInventoryBlock) entity).getContainer(side), side));
                }
                if (blockEntity instanceof PolyEnergyBlock) {
                    event.registerBlockEntity(Capabilities.Energy.BLOCK, blockEntityType, (entity, side) -> ((PolyEnergyBlock) entity).getEnergyStorage(side) == null ? null : new PolyNeoEnergyWrapper(((PolyEnergyBlock) entity).getEnergyStorage(side)));
                }
                if (blockEntity instanceof PolyFluidBlock) {
                    //TODO Capabilities
                    event.registerBlockEntity(Capabilities.Fluid.BLOCK, blockEntityType, (entity, side) -> ((PolyFluidBlock) entity).getFluidHandler(side) == null ? null : new PolyNeoFluidWrapper(((PolyFluidBlock) entity).getFluidHandler(side)));
                }

            } catch (Throwable ignored) {}
        }
    }

    private ResourceHandler<ItemResource> getInvWrapper(Container container, Direction side) {
        if (container == null) {
            return null;
        } else if (container instanceof WorldlyContainer worldlyContainer) {
            return new WorldlyContainerWrapper(worldlyContainer, side);
        } else {
            return VanillaContainerWrapper.of(container);
        }
    }
}
