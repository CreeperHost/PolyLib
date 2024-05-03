package net.creeperhost.polylib.inventory.power;

import com.mojang.datafixers.util.Pair;
import net.creeperhost.polylib.PolyLibPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface EnergyManager {

    /**
     * @return a new {@link IPolyEnergyStorage} instance for the specified block, or null if the block does not support energy handling.
     */
    @Nullable
    IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side);

    /**
     * @return a new {@link IPolyEnergyStorageItem} instance for the specified stack, or null if the stack does not support energy handling.
     * After modifying the energy stored in this handler, you must pull the newly modified ItemStack via {@link IPolyEnergyStorageItem#getContainer()}
     * The reference you previously had will be invalid on fabric.
     */
    @Nullable
    IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack);

    //###### Energy Utilities ######

    //=== Get Handler ===

    static IPolyEnergyStorage getHandler(BlockEntity tile, @Nullable Direction side) {
        return PolyLibPlatform.getEnergyManager().getBlockEnergyStorage(tile, side);
    }

    static IPolyEnergyStorageItem getHandler(ItemStack stack) {
        return stack.isEmpty() ? null : PolyLibPlatform.getEnergyManager().getItemEnergyStorage(stack);
    }

    // ================= Receive =================

    static long insertEnergy(BlockEntity tile, long energy, Direction side, boolean simulate) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        if (storage != null && storage.canReceive()) {
            return storage.receiveEnergy(energy, simulate);
        }
        return 0;
    }

    static long insertEnergy(ItemStack stack, long energy, boolean simulate) {
        IPolyEnergyStorage storage = getHandler(stack);
        if (storage != null && storage.canReceive()) {
            return storage.receiveEnergy(energy, simulate);
        }
        return 0;
    }

    // ================= Extract =================

    static long extractEnergy(BlockEntity tile, long energy, Direction side, boolean simulate) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        if (storage != null && storage.canExtract()) {
            return storage.extractEnergy(energy, simulate);
        }
        return 0;
    }

//    static long extractEnergy(ItemStack stack, long energy, boolean simulate) {
//        IPolyEnergyStorage storage = getHandler(stack);
//        if (storage != null && storage.canExtract()) {
//            return storage.extractEnergy(energy, simulate);
//        }
//        return 0;
//    }

    // ================= Transfer =================

    static long transferEnergy(IPolyEnergyStorage source, IPolyEnergyStorage target) {
        return target.receiveEnergy(source.extractEnergy(target.receiveEnergy(target.getMaxEnergyStored(), true), false), false);
    }

    static long transferEnergy(BlockEntity source, Direction sourceSide, IPolyEnergyStorage target) {
        IPolyEnergyStorage storage = getHandler(source, sourceSide);
        return storage == null ? 0 : transferEnergy(storage, target);
    }

    static long transferEnergy(IPolyEnergyStorage source, BlockEntity target, Direction targetSide) {
        IPolyEnergyStorage storage = getHandler(target, targetSide);
        return storage == null ? 0 : transferEnergy(source, storage);
    }

//    static long transferEnergy(ItemStack source, IPolyEnergyStorage target) {
//        IPolyEnergyStorage storage = getHandler(source);
//        return storage == null ? 0 : transferEnergy(storage, target);
//    }

//    static long transferEnergy(IPolyEnergyStorage source, ItemStack target) {
//        IPolyEnergyStorage storage = getHandler(target);
//        return storage == null ? 0 : transferEnergy(source, storage);
//    }

    static long transferEnergy(ItemStack source, BlockEntity target, Direction targetSide) {
        IPolyEnergyStorage storage = getHandler(source);
        return storage == null ? 0 : transferEnergy(storage, target, targetSide);
    }

//    static long transferEnergy(BlockEntity source, Direction sourceSide, ItemStack target) {
//        IPolyEnergyStorage storage = getHandler(target);
//        return storage == null ? 0 : transferEnergy(source, sourceSide, storage);
//    }

    static long transferEnergy(BlockEntity source, Direction sourceSide, BlockEntity target, Direction targetSide) {
        IPolyEnergyStorage sourceStorage = getHandler(source, sourceSide);
        if (sourceStorage == null) {
            return 0;
        }
        IPolyEnergyStorage targetHandler = getHandler(target, targetSide);
        return targetHandler == null ? 0 : transferEnergy(sourceStorage, targetHandler);
    }

    static void distributeEnergyNearby(BlockEntity source, long amount) {
        BlockPos blockPos = source.getBlockPos();
        Level level = source.getLevel();
        if (level == null || amount == 0) return;
        Direction.stream()
                .map(direction -> Pair.of(direction, level.getBlockEntity(blockPos.relative(direction))))
                .filter(pair -> pair.getSecond() != null)
                .map(pair -> Pair.of(getHandler(pair.getSecond(), pair.getFirst().getOpposite()), pair.getFirst()))
                .filter(pair -> pair.getFirst() != null)
                .forEach(pair -> transferEnergy(source, pair.getSecond(), pair.getFirst()));
    }

    static void distributeEnergyNearby(BlockEntity energyBlock) {
        distributeEnergyNearby(energyBlock, Long.MAX_VALUE);
    }

    // ================= Checks =================

    static boolean canExtractEnergy(ItemStack stack) {
        IPolyEnergyStorage storage = getHandler(stack);
        return storage != null && storage.canExtract();
    }

    static boolean canReceiveEnergy(ItemStack stack) {
        IPolyEnergyStorage storage = getHandler(stack);
        return storage != null && storage.canReceive();
    }

    static boolean canExtractEnergy(BlockEntity tile, Direction side) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        return storage != null && storage.canExtract();
    }

    static boolean canReceiveEnergy(BlockEntity tile, Direction side) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        return storage != null && storage.canReceive();
    }

    static long getEnergyStored(BlockEntity tile, Direction side) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        return storage == null ? 0 : storage.getEnergyStored();
    }

    static long getMaxEnergyStored(BlockEntity tile, Direction side) {
        IPolyEnergyStorage storage = getHandler(tile, side);
        return storage == null ? 0 : storage.getMaxEnergyStored();
    }

    static long getEnergyStored(ItemStack stack) {
        IPolyEnergyStorage storage = getHandler(stack);
        return storage == null ? 0 : storage.getEnergyStored();
    }

    static long getMaxEnergyStored(ItemStack stack) {
        IPolyEnergyStorage storage = getHandler(stack);
        return storage == null ? 0 : storage.getMaxEnergyStored();
    }

    static boolean isEnergyItem(ItemStack stack) {
        return getHandler(stack) != null;
    }

    static boolean isEnergyBlock(BlockEntity tile) {
        return getHandler(tile, null) != null;
    }

    static boolean isEnergyBlock(BlockEntity tile, Direction direction) {
        return getHandler(tile, null) != null;
    }
}
