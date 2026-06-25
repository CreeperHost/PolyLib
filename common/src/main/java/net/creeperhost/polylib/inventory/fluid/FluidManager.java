package net.creeperhost.polylib.inventory.fluid;

import com.mojang.datafixers.util.Pair;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Platform fluid access and convenience utilities.
 * <p>
 * All PolyLib fluid amounts are expressed in droplets. One bucket is
 * {@link #BUCKET} droplets and one millibucket is {@link #MILLIBUCKET} droplets.
 */
public interface FluidManager {
    /**
     * Number of droplets in one bucket.
     */
    long BUCKET = 81_000;

    /**
     * Number of droplets in one millibucket.
     */
    long MILLIBUCKET = 81;

    /**
     * Gets a fluid storage view for a block entity.
     *
     * @param block queried block entity
     * @param side queried side, or null for unsided access
     * @return fluid storage, or null if unsupported
     */
    @Nullable
    IPolyFluidStorage getBlockFluidStorage(BlockEntity block, @Nullable Direction side);

    /**
     * Gets a fluid storage view for an item stack.
     *
     * @param stack queried item stack
     * @return item fluid storage, or null if unsupported
     */
    @Nullable
    IPolyFluidStorageItem getItemFluidStorage(ItemStack stack);

    /**
     * Resolves a block fluid handler through the active platform.
     */
    static IPolyFluidStorage getHandler(BlockEntity tile, @Nullable Direction side) {
        return Services.PLATFORM.getFluidManager().getBlockFluidStorage(tile, side);
    }

    /**
     * Resolves an item fluid handler through the active platform.
     */
    static IPolyFluidStorageItem getHandler(ItemStack stack) {
        return stack.isEmpty() ? null : Services.PLATFORM.getFluidManager().getItemFluidStorage(stack);
    }

    /**
     * Attempts to fill a block fluid handler.
     */
    static long fill(BlockEntity tile, PolyFluidStack stack, Direction side, boolean simulate) {
        IPolyFluidStorage storage = getHandler(tile, side);
        return storage == null ? 0 : storage.fill(stack, simulate);
    }

    /**
     * Attempts to fill an item fluid handler.
     */
    static long fill(ItemStack stack, PolyFluidStack fluid, boolean simulate) {
        IPolyFluidStorage storage = getHandler(stack);
        return storage == null ? 0 : storage.fill(fluid, simulate);
    }

    /**
     * Attempts to drain a matching fluid from a block fluid handler.
     */
    static PolyFluidStack drain(BlockEntity tile, PolyFluidStack stack, Direction side, boolean simulate) {
        IPolyFluidStorage storage = getHandler(tile, side);
        return storage == null ? PolyFluidStack.EMPTY : storage.drain(stack, simulate);
    }

    /**
     * Attempts to drain any fluid from a block fluid handler.
     */
    static PolyFluidStack drain(BlockEntity tile, long amount, Direction side, boolean simulate) {
        IPolyFluidStorage storage = getHandler(tile, side);
        return storage == null ? PolyFluidStack.EMPTY : storage.drain(amount, simulate);
    }

    /**
     * Attempts to drain a matching fluid from an item fluid handler.
     */
    static PolyFluidStack drain(ItemStack stack, PolyFluidStack fluid, boolean simulate) {
        IPolyFluidStorage storage = getHandler(stack);
        return storage == null ? PolyFluidStack.EMPTY : storage.drain(fluid, simulate);
    }

    /**
     * Transfers as much compatible fluid as possible from source to target.
     *
     * @return amount transferred in droplets
     */
    static long transferFluid(IPolyFluidStorage source, IPolyFluidStorage target) {
        PolyFluidStack fluid = source.getFluid();
        if (fluid.isEmpty()) return 0;
        long fillable = target.fill(fluid.copyWithAmount(target.getCapacity()), true);
        return target.fill(source.drain(fluid.copyWithAmount(fillable), false), false);
    }

    /**
     * Transfers fluid from a block handler into a direct target handler.
     */
    static long transferFluid(BlockEntity source, Direction sourceSide, IPolyFluidStorage target) {
        IPolyFluidStorage storage = getHandler(source, sourceSide);
        return storage == null ? 0 : transferFluid(storage, target);
    }

    /**
     * Transfers fluid from a direct source handler into a block handler.
     */
    static long transferFluid(IPolyFluidStorage source, BlockEntity target, Direction targetSide) {
        IPolyFluidStorage storage = getHandler(target, targetSide);
        return storage == null ? 0 : transferFluid(source, storage);
    }

    /**
     * Transfers fluid between two block handlers.
     */
    static long transferFluid(BlockEntity source, Direction sourceSide, BlockEntity target, Direction targetSide) {
        IPolyFluidStorage sourceStorage = getHandler(source, sourceSide);
        IPolyFluidStorage targetStorage = getHandler(target, targetSide);
        return sourceStorage == null || targetStorage == null ? 0 : transferFluid(sourceStorage, targetStorage);
    }

    /**
     * Attempts to distribute fluid from a block entity to all neighboring block handlers.
     */
    static void distributeFluidNearby(BlockEntity source) {
        BlockPos blockPos = source.getBlockPos();
        Level level = source.getLevel();
        if (level == null) return;
        Direction.stream()
                .map(direction -> Pair.of(direction, level.getBlockEntity(blockPos.relative(direction))))
                .filter(pair -> pair.getSecond() != null)
                .map(pair -> Pair.of(getHandler(pair.getSecond(), pair.getFirst().getOpposite()), pair.getFirst()))
                .filter(pair -> pair.getFirst() != null)
                .forEach(pair -> transferFluid(source, pair.getSecond(), pair.getFirst()));
    }

    /**
     * @return true if the stack exposes a fluid handler
     */
    static boolean isFluidItem(ItemStack stack) {
        return getHandler(stack) != null;
    }

    /**
     * @return true if the block entity exposes an unsided fluid handler
     */
    static boolean isFluidBlock(BlockEntity tile) {
        return getHandler(tile, null) != null;
    }
}
