package net.creeperhost.polylib.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fluid handler interface based on Forge's IFluidHandler
 * Created by brandon3055 on 17/02/2024
 */
public interface PolyFluidHandler {

    int getTanks();

    @NotNull
    FluidStack getFluidInTank(int tank);

    long getTankCapacity(int tank);

    boolean isFluidValid(int tank, @NotNull FluidStack stack);

    /**
     * @param resource FluidStack attempting to fill the tank.
     * @param simulate If true, the fill will only be simulated.
     * @return Amount of fluid that was accepted (or would be, if simulated) by the tank.
     */
    long fill(FluidStack resource, boolean simulate);

    /**
     * @param maxDrain Maximum amount of fluid to be removed from the container.
     * @param simulate If true, the drain will only be simulated.
     * @return Amount of fluid that was removed (or would be, if simulated) from the tank.
     */
    @NotNull
    FluidStack drain(long maxDrain, boolean simulate);

    /**
     * @param resource Maximum amount of fluid to be removed from the container.
     * @param simulate If true, the drain will only be simulated.
     * @return FluidStack representing fluid that was removed (or would be, if simulated) from the tank.
     */
    @NotNull
    FluidStack drain(FluidStack resource, boolean simulate);

    /**
     * For internal use only!
     * This only exists because fabric needs to be able to revert a fluid transaction.
     */
    @Deprecated
    void _setFluidInTank(int tank, FluidStack fluidStack);
}
