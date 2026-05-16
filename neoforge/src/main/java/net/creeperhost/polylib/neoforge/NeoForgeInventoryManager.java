package net.creeperhost.polylib.neoforge;

import com.google.common.collect.Iterables;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public class NeoForgeInventoryManager
{
    public static void init(IEventBus bus)
    {
        bus.addListener(EventPriority.LOWEST, NeoForgeInventoryManager::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        Constants.LOG.info("=====Registering Capabilities=====");
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof PolyEnergyItem polyEnergyItem) {
                event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new PolyNeoEnergyItemWrapper(polyEnergyItem.getEnergyStorage(stack), itemAccess), item);
            }
        }

        for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE)
        {
            try
            {
                Identifier reg = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
                ModContainer mod = ModList.get().getModContainerById(reg.getNamespace()).orElse(null);
                if (mod == null || mod.getModInfo().getDependencies().stream().noneMatch(e -> e.getModId().equals(Constants.MOD_ID))) {
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
//                if (blockEntity instanceof PolyFluidBlock) {
//                    event.registerBlockEntity(Capabilities.Fluid.BLOCK, blockEntityType, (entity, side) -> ((PolyFluidBlock) entity).getFluidHandler(side) == null ? null : new PolyNeoFluidWrapper(((PolyFluidBlock) entity).getFluidHandler(side)));
//                }
            } catch (Exception e) {
                Constants.LOG.error("Error injecting capabilities", e);
            }
        }
    }

    private static ResourceHandler<ItemResource> getInvWrapper(Container container, Direction side) {
        if (container == null) {
            return null;
        } else if (container instanceof WorldlyContainer worldlyContainer) {
            return new WorldlyContainerWrapper(worldlyContainer, side);
        } else {
            return VanillaContainerWrapper.of(container);
        }
    }
}
