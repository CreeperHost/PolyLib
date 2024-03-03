package net.creeperhost.polylib.data.serializable;

import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The base class of a simple general purpose serializable data system.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public abstract class AbstractDataStore<T> {
    private boolean isDirty = true;
    protected T value;
    protected Predicate<T> validator = value -> true;

    public AbstractDataStore(T defaultValue) {
        this.value = defaultValue;
    }

    public AbstractDataStore<T> setValidator(Predicate<T> validator) {
        this.validator = validator;
        return this;
    }

    @Deprecated
    public T getValue() {
        return get();
    }

    public T get() {
        return value;
    }

    @Deprecated
    public void setValue(T value) {
        set(value);
    }

    public T set(T value) {
        if (!Objects.equals(value, this.value) && validator.test(value)) {
            this.value = value;
            markDirty();
        }
        return this.value;
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean isDirty(boolean reset) {
        if (isDirty) {
            isDirty = !reset;
            return true;
        }
        return false;
    }

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void fromBytes(FriendlyByteBuf buf);

    public abstract Tag toTag();

    public abstract void fromTag(Tag tag);

    public boolean isSameValue(T newValue) {
        return Objects.equals(value, newValue);
    }

    protected T validValue(T value, T fallBack) {
        return validator.test(value) ? value : fallBack;
    }
}

