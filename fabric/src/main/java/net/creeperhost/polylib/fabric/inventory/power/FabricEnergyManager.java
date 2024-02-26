package net.creeperhost.polylib.fabric.inventory.power;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class FabricEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        EnergyStorage storage = EnergyStorage.SIDED.find(block.getLevel(), block.getBlockPos(), block.getBlockState(), block, side);
        if (storage == null) return null;
        return new FabricPolyEnergyWrapper(storage);
    }

    @Override
    public @Nullable IPolyEnergyStorage getItemEnergyStorage(ItemStack stack) {
        ContainerItemContext context = ContainerItemContext.withInitial(stack);
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, context);
        if (storage == null) return null;
        return new FabricPolyEnergyWrapper(storage);
    }
}
