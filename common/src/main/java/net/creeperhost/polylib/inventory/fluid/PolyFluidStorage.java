package net.creeperhost.polylib.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fluid storage interface based on Forge's IFluidTank
 * Created by brandon3055 on 15/02/2024
 */
public interface PolyFluidStorage {

    @NotNull
    FluidStack getFluid();

    /**
     * @return Current amount of fluid in the tank.
     */
    long getFluidAmount();

    /**
     * @return Capacity of this fluid tank.
     */
    long getCapacity();

    /**
     * @param stack Fluidstack holding the Fluid to be queried.
     * @return If the tank can hold the fluid (EVER, not at the time of query).
     */
    boolean isFluidValid(FluidStack stack);

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

    void markDirty();
}
