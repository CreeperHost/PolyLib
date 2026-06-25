package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The base class of a simple general purpose serializable data system.
 * <p>
 * A data store owns one value, tracks whether that value needs to be synchronized
 * or saved, and defines the byte-buffer and tag serialization used by
 * {@code TileDataManager}.
 *
 * @param <T> the value type stored by this data object
 */
public abstract class AbstractDataStore<T> {
    private boolean isDirty = true;
    protected T value;
    protected Predicate<T> validator = value -> true;

    /**
     * Creates a data store with the supplied default value.
     *
     * @param defaultValue the initial value
     */
    public AbstractDataStore(T defaultValue) {
        this.value = defaultValue;
    }

    /**
     * Sets a validator used to reject future values.
     * <p>
     * The validator is applied by {@link #set} and by deserialization helpers
     * that call {@link #validValue}.
     *
     * @param validator predicate that returns true for accepted values
     * @return this data store
     */
    public AbstractDataStore<T> setValidator(Predicate<T> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * @return the currently stored value
     * @deprecated use {@link #get()}
     */
    @Deprecated(forRemoval = true)
    public T getValue() {
        return get();
    }

    /**
     * @return the currently stored value
     */
    public T get() {
        return value;
    }

    /**
     * Updates the stored value if it is accepted by the validator.
     *
     * @param value the requested new value
     * @deprecated use {@link #set}
     */
    @Deprecated(forRemoval = true)
    public void setValue(T value) {
        set(value);
    }

    /**
     * Updates the stored value if it differs from the current value and passes validation.
     * <p>
     * Accepted changes mark this store dirty.
     *
     * @param value the requested new value
     * @return the value currently stored after validation
     */
    public T set(T value) {
        if (!Objects.equals(value, this.value) && validator.test(value)) {
            this.value = value;
            markDirty();
        }
        return this.value;
    }

    /**
     * Marks this data store as needing synchronization or saving.
     */
    public void markDirty() {
        isDirty = true;
    }

    /**
     * Checks whether this data store has changed since the last reset.
     *
     * @param reset true to clear the dirty flag when it is observed
     * @return true if the store was dirty
     */
    public boolean isDirty(boolean reset) {
        if (isDirty) {
            isDirty = !reset;
            return true;
        }
        return false;
    }

    /**
     * Writes this value to a network buffer.
     *
     * @param buf the destination buffer
     */
    public abstract void toBytes(RegistryFriendlyByteBuf buf);

    /**
     * Reads this value from a network buffer.
     *
     * @param buf the source buffer
     */
    public abstract void fromBytes(RegistryFriendlyByteBuf buf);

    /**
     * Writes this value to persistent tag data.
     *
     * @param output the tag output
     */
    public abstract void toTag(ValueOutput output);

    /**
     * Reads this value from persistent tag data.
     *
     * @param input the tag input
     */
    public abstract void fromTag(ValueInput input);

    /**
     * Compares a candidate value with the currently stored value.
     *
     * @param newValue the value to compare
     * @return true when the values should be treated as equal
     */
    public boolean isSameValue(T newValue) {
        return Objects.equals(value, newValue);
    }

    /**
     * Applies the validator to a deserialized value.
     *
     * @param value    the value to validate
     * @param fallBack the value to keep when validation fails
     * @return {@code value} when accepted, otherwise {@code fallBack}
     */
    protected T validValue(T value, T fallBack) {
        return validator.test(value) ? value : fallBack;
    }
}

