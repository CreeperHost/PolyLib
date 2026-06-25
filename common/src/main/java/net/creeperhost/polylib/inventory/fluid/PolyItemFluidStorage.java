package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ItemStack backed fluid storage using PolyLib's item fluid data component.
 */
public class PolyItemFluidStorage extends PolyFluidStorage implements IPolyFluidStorageItem {
    private final ItemStack stack;

    /**
     * @param stack item stack to read and write fluid contents on
     * @param capacity tank capacity in droplets
     */
    public PolyItemFluidStorage(ItemStack stack, long capacity) {
        super(capacity);
        this.stack = stack;
        loadFluid();
    }

    /**
     * @param stack item stack to read and write fluid contents on
     * @param capacity tank capacity in droplets
     * @param changeListener optional listener invoked before the item component is updated
     */
    public PolyItemFluidStorage(ItemStack stack, long capacity, Runnable changeListener) {
        super(capacity, changeListener);
        this.stack = stack;
        loadFluid();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        saveFluid();
    }

    private void loadFluid() {
        fluid = stack.getOrDefault(getFluidComponent(), PolyFluidStack.EMPTY);
    }

    private void saveFluid() {
        if (fluid.isEmpty()) {
            stack.remove(getFluidComponent());
        } else {
            stack.set(getFluidComponent(), fluid);
        }
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return stack;
    }
}
