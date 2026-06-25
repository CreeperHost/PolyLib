package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for a boolean value.
 */
public class BooleanData extends AbstractDataStore<Boolean> {

    /**
     * Creates a boolean data store with a default value of false.
     */
    public BooleanData() {
        super(false);
    }

    /**
     * Creates a boolean data store.
     *
     * @param defaultValue the initial value
     */
    public BooleanData(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readBoolean(), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putBoolean("value", value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.getBooleanOr("value", value);
    }

    /**
     * Invert the value stored in this {@link BooleanData} and return the result.
     *
     * @return the new stored value
     */
    public boolean invert() {
        return set(!value);
    }
}
