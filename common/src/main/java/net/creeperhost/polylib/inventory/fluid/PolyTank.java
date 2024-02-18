package net.creeperhost.polylib.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Created by brandon3055 on 16/02/2024
 */
public class PolyTank implements PolyFluidStorage, PolyFluidHandler {
    protected Predicate<FluidStack> validator;
    protected Runnable dirtyListener;
    @NotNull
    protected FluidStack fluid = FluidStack.empty();
    protected long capacity;

    public PolyTank(long capacity) {
        this(capacity, e -> true, null);
    }

    public PolyTank(long capacity, Predicate<FluidStack> validator) {
        this(capacity, validator, null);
    }

    public PolyTank(long capacity, Predicate<FluidStack> validator, @Nullable Runnable dirtyListener) {
        this.capacity = capacity;
        this.validator = validator;
        this.dirtyListener = dirtyListener;
    }

    public PolyTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public PolyTank setValidator(@Nullable Predicate<FluidStack> validator) {
        this.validator = validator == null ? e -> true : validator;
        return this;
    }

    public PolyTank setDirtyListener(Runnable dirtyListener) {
        this.dirtyListener = dirtyListener;
        return this;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return fluid;
    }

    @Override
    public long getFluidAmount() {
        return fluid.getAmount();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public long getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack);
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (simulate) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = FluidStack.create(resource, Math.min(capacity, resource.getAmount()));
            markDirty();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        long filled = capacity - fluid.getAmount();
        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0) {
            markDirty();
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        long drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = FluidStack.create(fluid, drained);
        if (!simulate && drained > 0) {
            fluid.shrink(drained);
            markDirty();
        }
        return stack;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.empty();
        }
        return drain(resource.getAmount(), simulate);
    }

    @Override
    public void _setFluidInTank(int tank, FluidStack fluidStack) {
        fluid = fluidStack;
    }

    @Override
    public void markDirty() {
        if (dirtyListener != null) dirtyListener.run();
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public void readFromNBT(CompoundTag nbt) {
        fluid = FluidStack.read(nbt);
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        fluid.write(nbt);
        return nbt;
    }

    public void readFromBuf(FriendlyByteBuf buf) {
        fluid.write(buf);
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        fluid = FluidStack.read(buf);
    }
}
