package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.fabric.inventory.InitialContentsContainerItemContext;
import net.creeperhost.polylib.fabric.inventory.power.FabricPolyEnergyItemWrapper;
import net.creeperhost.polylib.fabric.inventory.power.FabricPolyEnergyWrapper;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyItemWrapper;
import net.creeperhost.polylib.fabric.inventory.power.PolyFabricEnergyWrapper;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class TeamRebornEnergyCompat
{
    public static void init()
    {
        EnergyStorage.ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof PolyEnergyItem item) {
                if (item.getEnergyStorage(itemStack) instanceof IPolyEnergyStorageItem storage){
                    return new PolyFabricEnergyItemWrapper(storage, context);
                }
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

    public static boolean isEnergyContainer(BlockEntity block, Direction direction)
    {
        return EnergyStorage.SIDED.find(block.getLevel(), block.getBlockPos(), direction) != null;
    }

    public static @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        EnergyStorage storage = EnergyStorage.SIDED.find(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (storage == null) return null;
        return new FabricPolyEnergyWrapper(storage);
    }

    public static @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        ContainerItemContext context = new InitialContentsContainerItemContext(stack);
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, context);
        if (storage == null) return null;
        return new FabricPolyEnergyItemWrapper(storage, context);
    }
}
