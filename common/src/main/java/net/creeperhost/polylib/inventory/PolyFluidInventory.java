package net.creeperhost.polylib.inventory;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.nbt.CompoundTag;

public class PolyFluidInventory
{
    private FluidStack fluidStack = FluidStack.empty();
    private int capacity;

    public PolyFluidInventory(int capacity)
    {
        this.capacity = capacity;
        if(!fluidStack.isEmpty())
            this.fluidStack.setAmount(0);
    }

    public PolyFluidInventory(FluidStack fluidStack, int capacity)
    {
        this.fluidStack = fluidStack;
        this.capacity = capacity;
    }

    public FluidStack getFluidStack()
    {
        return fluidStack;
    }

    public void setFluidStack(FluidStack fluidStack)
    {
        this.fluidStack = fluidStack;
        setChanged();
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
        setChanged();
    }

    public void setChanged()
    {

    }

    public CompoundTag serializeNBT()
    {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("capacity", getCapacity());
        FluidStackHooks.write(getFluidStack(), compoundTag);

        return compoundTag;
    }

    public void deserializeNBT(CompoundTag compoundTag)
    {
        setCapacity(compoundTag.getInt("capacity"));
        setFluidStack(FluidStackHooks.read(compoundTag));
    }
}
