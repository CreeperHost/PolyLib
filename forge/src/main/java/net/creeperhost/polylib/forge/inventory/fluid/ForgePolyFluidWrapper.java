package net.creeperhost.polylib.forge.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 17/02/2024
 */
public class ForgePolyFluidWrapper implements PolyFluidHandler {

    private final IFluidHandler handler;

    public ForgePolyFluidWrapper(IFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return FluidStackHooksForge.fromForge(handler.getFluidInTank(tank));
    }

    @Override
    public long getTankCapacity(int tank) {
        return Math.min(Integer.MAX_VALUE, handler.getTankCapacity(tank));
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack) {
        return handler.isFluidValid(tank, FluidStackHooksForge.toForge(fluidStack));
    }

    @Override
    public long fill(FluidStack fluidStack, boolean simulate) {
        return handler.fill(FluidStackHooksForge.toForge(fluidStack), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack fluidStack, boolean simulate) {
        return FluidStackHooksForge.fromForge(handler.drain(FluidStackHooksForge.toForge(fluidStack), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE));
    }

    @Override
    public void _setFluidInTank(int tank, FluidStack fluidStack) {
        //No-op This should never be called.
    }

    @Override
    public @NotNull FluidStack drain(long amount, boolean simulate) {
        return FluidStackHooksForge.fromForge(handler.drain((int) Math.max(amount, Integer.MAX_VALUE), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE));
    }
}
