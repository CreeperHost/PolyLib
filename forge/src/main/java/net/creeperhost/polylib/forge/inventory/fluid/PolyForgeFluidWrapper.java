package net.creeperhost.polylib.forge.inventory.fluid;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.creeperhost.polylib.inventory.fluid.PolyFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 15/02/2024
 */
public class PolyForgeFluidWrapper implements IFluidHandler {

    private final PolyFluidHandler handler;

    public PolyForgeFluidWrapper(PolyFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int i) {
        return FluidStackHooksForge.toForge(handler.getFluidInTank(i));
    }

    @Override
    public int getTankCapacity(int i) {
        return (int) Math.min(Integer.MAX_VALUE, handler.getTankCapacity(i));
    }

    @Override
    public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
        return handler.isFluidValid(i, FluidStackHooksForge.fromForge(fluidStack));
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return (int) handler.fill(FluidStackHooksForge.fromForge(fluidStack), fluidAction.simulate());
    }

    @Override
    public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        return FluidStackHooksForge.toForge(handler.drain(FluidStackHooksForge.fromForge(fluidStack), fluidAction.simulate()));
    }

    @Override
    public @NotNull FluidStack drain(int i, FluidAction fluidAction) {
        return FluidStackHooksForge.toForge(handler.drain(i, fluidAction.simulate()));
    }
}
