package net.creeperhost.polylib.data.serializable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class IntData extends AbstractDataStore<Integer> {

    public IntData() {
        super(0);
    }

    public IntData(int defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readVarInt(), value);
    }

    @Override
    public Tag toTag(HolderLookup.Provider provider) {
        return IntTag.valueOf(value);
    }

    @Override
    public void fromTag(HolderLookup.Provider provider, Tag tag) {
        value = validValue(((NumericTag) tag).getAsInt(), value);
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
    public int add(int add) {
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
    public int subtract(int subtract) {
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
    public int multiply(int multiplyBy) {
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
    public int divide(int divideBy) {
        return set(get() / divideBy);
    }

    /**
     * Reset this data to zero.
     *
     * @return zero.
     */
    public int zero() {
        return set(0);
    }

    /**
     * Increment by 1;
     *
     * @return The new value stored in this data object.
     */
    public int inc() {
        return add(1);
    }

    /**
     * Decrement by 1;
     *
     * @return The new value stored in this data object.
     */
    public int dec() {
        return subtract(1);
    }
}
