package net.creeperhost.polylib.inventory.fluid;

import net.creeperhost.polylib.util.Serializable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Simple single-tank implementation of {@link IPolyFluidStorage}.
 * <p>
 * This storage accepts one fluid type at a time and calls an optional change
 * listener whenever its contents are mutated.
 */
public class PolyFluidStorage implements IPolyFluidStorage, Serializable {
    protected Runnable changeListener;
    protected PolyFluidStack fluid = PolyFluidStack.EMPTY;
    protected long capacity;
    protected boolean allowDrain = true;
    protected boolean allowFill = true;

    /**
     * Creates a fluid storage with the supplied capacity.
     *
     * @param capacity tank capacity in droplets
     */
    public PolyFluidStorage(long capacity) {
        this(capacity, null);
    }

    /**
     * Creates a fluid storage with the supplied capacity and change listener.
     *
     * @param capacity tank capacity in droplets
     * @param changeListener callback invoked after content changes
     */
    public PolyFluidStorage(long capacity, Runnable changeListener) {
        this.capacity = capacity;
        this.changeListener = changeListener;
    }

    /**
     * Sets whether fluid can be drained or filled.
     *
     * @param allowDrain true to allow draining
     * @param allowFill true to allow filling
     * @return this storage
     */
    public PolyFluidStorage setIOMode(boolean allowDrain, boolean allowFill) {
        this.allowDrain = allowDrain;
        this.allowFill = allowFill;
        return this;
    }

    /**
     * @return this storage configured for draining only
     */
    public PolyFluidStorage setDrainOnly() {
        return setIOMode(true, false);
    }

    /**
     * @return this storage configured for filling only
     */
    public PolyFluidStorage setFillOnly() {
        return setIOMode(false, true);
    }

    /**
     * Updates capacity and clamps current contents if needed.
     *
     * @param capacity new capacity in droplets
     * @return this storage
     */
    public PolyFluidStorage setCapacity(long capacity) {
        this.capacity = capacity;
        if (fluid.getAmount() > capacity) {
            setFluid(fluid.copyWithAmount(capacity));
        }
        return this;
    }

    @Override
    public PolyFluidStack getFluid() {
        return fluid;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long fill(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty() || !canFill(resource)) {
            return 0;
        }

        long filled = Math.min(capacity - fluid.getAmount(), resource.getAmount());
        if (!simulate && filled > 0) {
            fluid = fluid.isEmpty() ? resource.copyWithAmount(filled) : fluid.copyWithAmount(fluid.getAmount() + filled);
            markDirty();
        }
        return filled;
    }

    @Override
    public PolyFluidStack drain(PolyFluidStack resource, boolean simulate) {
        if (resource == null || resource.isEmpty() || !canDrain(resource)) {
            return PolyFluidStack.EMPTY;
        }
        return drain(resource.getAmount(), simulate);
    }

    @Override
    public PolyFluidStack drain(long maxDrain, boolean simulate) {
        if (fluid.isEmpty() || maxDrain <= 0 || !canDrain(fluid)) {
            return PolyFluidStack.EMPTY;
        }

        long drained = Math.min(fluid.getAmount(), maxDrain);
        PolyFluidStack result = fluid.copyWithAmount(drained);
        if (!simulate) {
            fluid = fluid.copyWithAmount(fluid.getAmount() - drained);
            markDirty();
        }
        return result;
    }

    @Override
    public boolean canFill(PolyFluidStack resource) {
        return allowFill && isFluidValid(resource) && (fluid.isEmpty() || fluid.isSameFluid(resource)) && fluid.getAmount() < capacity;
    }

    @Override
    public boolean canDrain(PolyFluidStack resource) {
        return allowDrain && !fluid.isEmpty() && fluid.isSameFluid(resource);
    }

    @Override
    public boolean isFluidValid(PolyFluidStack resource) {
        return resource != null && !resource.isEmpty();
    }

    @Override
    public void setFluid(PolyFluidStack stack) {
        if (stack == null || stack.isEmpty()) {
            fluid = PolyFluidStack.EMPTY;
        } else {
            fluid = stack.copyWithAmount(Math.min(stack.getAmount(), capacity));
        }
        markDirty();
    }

    /**
     * Marks this storage as changed and runs the optional listener.
     */
    public void markDirty() {
        if (changeListener != null) changeListener.run();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.store("fluid", PolyFluidStack.CODEC, fluid);
    }

    @Override
    public void deserialize(ValueInput input) {
        fluid = input.read("fluid", PolyFluidStack.CODEC).orElse(PolyFluidStack.EMPTY);
        if (fluid.getFluid() == Fluids.EMPTY) {
            fluid = PolyFluidStack.EMPTY;
        }
    }

    /**
     * Reads this storage from a network buffer.
     *
     * @param buf buffer to read from
     */
    public void readFromBuf(FriendlyByteBuf buf) {
        PolyFluidStack.STREAM_CODEC.encode((net.minecraft.network.RegistryFriendlyByteBuf) buf, fluid);
    }

    /**
     * Writes this storage to a network buffer.
     *
     * @param buf buffer to write to
     */
    public void writeToBuf(FriendlyByteBuf buf) {
        fluid = PolyFluidStack.STREAM_CODEC.decode((net.minecraft.network.RegistryFriendlyByteBuf) buf);
    }
}
