package net.creeperhost.polylib.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.PolyLibPlatform;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 15/02/2024
 */
public interface FluidManager {

    /**
     * The fluid value representing 1 bucket on the current platform.
     */
    long BUCKET = FluidStack.bucketAmount();

    /**
     * The fluid value representing 1/1000th of a bucket (1 milli-bucket) on the current platform.
     * <p>
     * If you just want to do everything in MilliBuckets and not worry about fabrics units,
     * Then use this as your reference for "what is a millibucket"
     * On forge this value is 1, on fabric it is 81
     */
    long MILLIBUCKET = BUCKET / 1000L;

    /**
     * This is a simple helper method that will convert a given milli-bucket fluid value
     * to the appropriate value for the current platform.
     *
     * @param milliBuckets Provide a milli-bucket fluid value.
     * @return the appropriate fluid value for teh current platform.
     */
    default long convert(long milliBuckets) {
        return milliBuckets * MILLIBUCKET;
    }

    /**
     * Returns a fluid handler instance for the specified BlockEntity if that entity supports forge/fabric fluid handling.
     * The returned handler is a wrapper that wraps the platform specific fluid implementation.
     * This wrapper does not account for things like capability invalidation, so you should not hold into it.
     * <p>
     * Another very important thing to note is that, depending on the current platform,
     * 1 Bucket will either equal 1000 MilliBuckets OR 81000 "Fabric Units"
     * Use {@link FluidStack#bucketAmount()} to figure out what the current value of a bucket is.
     *
     * @return a new {@link PolyFluidHandler} instance for the specified block, or null if the block does not support fluid handling.
     */
    @Nullable
    PolyFluidHandler getBlockFluidHandler(BlockEntity block, @Nullable Direction side);

    /**
     * Returns a fluid handler instance for the specified {@link ItemStack} if the stack supports forge/fabric fluid handling.
     * Note, after modifying the handlers contents the supplied {@link ItemStack} instance may no longer be valid!
     * To get the new {@link ItemStack} instance use {@link PolyFluidHandlerItem#getContainer()}
     * <p>
     * Another very important thing to note is that, depending on the current platform,
     * 1 Bucket will either equal 1000 MilliBuckets OR 81000 "Fabric Units"
     * Use {@link FluidStack#bucketAmount()} to figure out what the current value of a bucket is.
     *
     * @return a new {@link PolyFluidHandlerItem} instance for the specified ItemStack, or null if the stack does not support fluid handling.
     */
    @Nullable
    PolyFluidHandlerItem getItemFluidHandler(ItemStack stack);

    //###### Fluid Utilities ######

    //=== Get Handler ===

    static PolyFluidHandler getHandler(BlockEntity tile, @Nullable Direction side) {
        return PolyLibPlatform.getFluidManager().getBlockFluidHandler(tile, side);
    }

    static PolyFluidHandlerItem getHandler(ItemStack stack) {
        return PolyLibPlatform.getFluidManager().getItemFluidHandler(stack);
    }

    //=== Insert / Extract ===

    static long insertFluid(BlockEntity tile, FluidStack fluidStack, Direction side, boolean simulate) {
        PolyFluidHandler handler = getHandler(tile, side);
        if (handler == null) return 0;
        return handler.fill(fluidStack, simulate);
    }

    static FluidStack extractFluid(BlockEntity tile, FluidStack fluidStack, Direction side, boolean simulate) {
        PolyFluidHandler handler = getHandler(tile, side);
        if (handler == null) return FluidStack.empty();
        return handler.drain(fluidStack, simulate);
    }

    //=== Transfer first available fluid ===

    static FluidStack transferFluid(@Nullable PolyFluidHandler source, @Nullable PolyFluidHandler target) {
        if (source == null || target == null) return FluidStack.empty();
        //TODO, This is a little nasty. But we need to know what can be extracted,
        // Then we need to check how much of that can actually be inserted,
        // THEN... we need to make sure the 'insertable amount' can be extracted,
        // Because you cant extract less than a full bucket from a bucket.
        // There may be a better way to do this. But I cant think of one right now.
        FluidStack maxDrain = source.drain(Integer.MAX_VALUE, true);
        long maxFill = target.fill(maxDrain.copy(), true);
        maxDrain.setAmount(maxFill);
        maxDrain = source.drain(maxDrain, true);
        if (maxDrain.isEmpty()) return FluidStack.empty();
        return source.drain(target.fill(maxDrain, false), false);
    }

    static FluidStack transferFluid(BlockEntity source, Direction sourceSide, @Nullable PolyFluidHandler target) {
        PolyFluidHandler sourceHandler = getHandler(source, sourceSide);
        return transferFluid(sourceHandler, target);
    }

    static FluidStack transferFluid(@Nullable PolyFluidHandler source, BlockEntity target, Direction targetSide) {
        PolyFluidHandler targetHandler = getHandler(target, targetSide);
        return transferFluid(source, targetHandler);
    }

    static FluidStack transferFluid(BlockEntity source, Direction sourceSide, BlockEntity target, Direction targetSide) {
        PolyFluidHandler sourceHandler = getHandler(source, sourceSide);
        PolyFluidHandler targetHandler = getHandler(target, targetSide);
        return transferFluid(sourceHandler, targetHandler);
    }

    //=== Transfer specific fluid ===

    static long transferFluid(FluidStack fluidStack, PolyFluidHandler source, PolyFluidHandler target) {
        long canFill = target.fill(fluidStack, true);
        return target.fill(source.drain(fluidStack.copyWithAmount(canFill), false), false);
    }

    static long transferFluid(FluidStack fluidStack, BlockEntity source, Direction sourceSide, PolyFluidHandler target) {
        PolyFluidHandler handler = getHandler(source, sourceSide);
        return handler == null ? 0 : transferFluid(fluidStack, handler, target);
    }

    static long transferFluid(FluidStack fluidStack, PolyFluidHandler source, BlockEntity target, Direction targetSide) {
        PolyFluidHandler handler = getHandler(target, targetSide);
        return handler == null ? 0 : transferFluid(fluidStack, source, handler);
    }

    static long transferFluid(FluidStack fluidStack, BlockEntity source, Direction sourceSide, BlockEntity target, Direction targetSide) {
        PolyFluidHandler sourceStorage = getHandler(source, sourceSide);
        if (sourceStorage == null) {
            return 0;
        }
        PolyFluidHandler targetHandler = getHandler(target, targetSide);
        return targetHandler == null ? 0 : transferFluid(fluidStack, sourceStorage, targetHandler);
    }

    //=== Checks ===

    static boolean isFluidItem(ItemStack stack) {
        return getHandler(stack) != null;
    }

    static boolean isFluidBlock(BlockEntity tile, @Nullable Direction side) {
        return getHandler(tile, side) != null;
    }
}
