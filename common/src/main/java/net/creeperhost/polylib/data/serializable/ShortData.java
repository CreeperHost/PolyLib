package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for a short value.
 */
public class ShortData extends AbstractDataStore<Short> {

    /**
     * Creates a short data store with a default value of zero.
     */
    public ShortData() {
        super((short) 0);
    }

    /**
     * Creates a short data store.
     *
     * @param defaultValue the initial value
     */
    public ShortData(short defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeShort(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readShort(), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putShort("value", value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = (short) input.getShortOr("value", value);
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
    public short add(short add) {
        return set((short) (get() + add));
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
    public short subtract(short subtract) {
        return set((short) (get() - subtract));
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
    public short multiply(short multiplyBy) {
        return set((short) (get() * multiplyBy));
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
    public short divide(short divideBy) {
        return set((short) (get() / divideBy));
    }

    /**
     * Reset this data to zero.
     *
     * @return zero.
     */
    public short zero() {
        return set((short) 0);
    }

    /**
     * Increment by 1;
     *
     * @return The new value stored in this data object.
     */
    public short inc() {
        return add((short) 1);
    }

    /**
     * Decrement by 1;
     *
     * @return The new value stored in this data object.
     */
    public short dec() {
        return subtract((short) 1);
    }
}
