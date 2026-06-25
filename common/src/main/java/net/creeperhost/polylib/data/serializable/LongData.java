package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for a long value.
 */
public class LongData extends AbstractDataStore<Long> {

    /**
     * Creates a long data store with a default value of zero.
     */
    public LongData() {
        super(0L);
    }

    /**
     * Creates a long data store.
     *
     * @param defaultValue the initial value
     */
    public LongData(long defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeVarLong(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readVarLong(), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putLong("value", value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.getLongOr("value", value);
    }

    //=============== Helpers ===============

    /**
     * Add to the current value then return the result.
     * New value is automatically stored in this data object.
     * <br>
     * Equivalent to: 'data.value += v'
     *
     * @param add The value to add.
     * @return The new value stored in this data object.
     */
    public long add(long add) {
        return set(get() + add);
    }

    /**
     * Subtract to the current value then return the result.
     * New value is automatically stored in this data object.
     * <br>
     * Equivalent to: 'data.value -= v'
     *
     * @param subtract The value to subtract.
     * @return The new value stored in this data object.
     */
    public long subtract(long subtract) {
        return set(get() - subtract);
    }

    /**
     * Multiply to the current value by this amount then return the result.
     * New value is automatically stored in this data object.
     * <br>
     * Equivalent to: 'data.value *= v'
     *
     * @param multiplyBy The value to multiply by.
     * @return The new value stored in this data object.
     */
    public long multiply(long multiplyBy) {
        return set(get() * multiplyBy);
    }

    /**
     * Divide to the current value by this amount then return the result.
     * New value is automatically stored in this data object.
     * <br>
     * Equivalent to: 'data.value /= v'
     *
     * @param divideBy The value to divide by.
     * @return The new value stored in this data object.
     */
    public long divide(long divideBy) {
        return set(get() / divideBy);
    }

    /**
     * Reset this data to zero.
     *
     * @return zero.
     */
    public long zero() {
        return set(0L);
    }

    /**
     * Increment by 1;
     *
     * @return The new value stored in this data object.
     */
    public long inc() {
        return add(1);
    }

    /**
     * Decrement by 1;
     *
     * @return The new value stored in this data object.
     */
    public long dec() {
        return subtract(1);
    }
}
