package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for a double value.
 */
public class DoubleData extends AbstractDataStore<Double> {

    /**
     * Creates a double data store with a default value of zero.
     */
    public DoubleData() {
        super(0D);
    }

    /**
     * Creates a double data store.
     *
     * @param defaultValue the initial value
     */
    public DoubleData(double defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeDouble(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readDouble(), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putDouble("value", value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.getDoubleOr("value", value);
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
    public double add(double add) {
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
    public double subtract(double subtract) {
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
    public double multiply(double multiplyBy) {
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
    public double divide(double divideBy) {
        return set(get() / divideBy);
    }

    /**
     * Reset this data to zero.
     *
     * @return zero.
     */
    public double zero() {
        return set(0D);
    }

    /**
     * Increment by 1;
     *
     * @return The new value stored in this data object.
     */
    public double inc() {
        return add(1);
    }

    /**
     * Decrement by 1;
     *
     * @return The new value stored in this data object.
     */
    public double dec() {
        return subtract(1);
    }
}
