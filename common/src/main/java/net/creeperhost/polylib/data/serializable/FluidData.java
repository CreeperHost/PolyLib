package net.creeperhost.polylib.data.serializable;

import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for a {@link PolyFluidStack}.
 * <p>
 * Stored fluid stacks are copied on assignment, and equality is checked using
 * {@link PolyFluidStack#equals(Object)} so fluid, amount, and component data all
 * participate in dirty-state detection.
 */
public class FluidData extends AbstractDataStore<PolyFluidStack> {

    /**
     * Creates a fluid data store with {@link PolyFluidStack#EMPTY}.
     */
    public FluidData() {
        super(PolyFluidStack.EMPTY);
    }

    /**
     * Creates a fluid data store.
     *
     * @param defaultValue the initial fluid stack
     */
    public FluidData(PolyFluidStack defaultValue) {
        super(defaultValue);
    }

    /**
     * Stores a copy of the supplied fluid stack when it differs from the current
     * stack and passes validation.
     *
     * @param value the requested new fluid stack
     * @return the fluid stack currently stored after validation
     */
    @Override
    public PolyFluidStack set(PolyFluidStack value) {
        PolyFluidStack newValue = value == null ? PolyFluidStack.EMPTY : value;
        if (!newValue.equals(this.value) && validator.test(newValue)) {
            this.value = newValue.copy();
            markDirty();
        }
        return this.value;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        PolyFluidStack.STREAM_CODEC.encode(buf, value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(PolyFluidStack.STREAM_CODEC.decode(buf), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.store("value", PolyFluidStack.CODEC, value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.read("value", PolyFluidStack.CODEC).orElse(PolyFluidStack.EMPTY);
    }

    @Override
    public boolean isSameValue(PolyFluidStack newValue) {
        return value.equals(newValue == null ? PolyFluidStack.EMPTY : newValue);
    }
}
