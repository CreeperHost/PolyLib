package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class BooleanData extends AbstractDataStore<Boolean> {

    public BooleanData() {
        super(false);
    }

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
     * @return the nwe stored value.
     */
    public boolean invert() {
        return set(!value);
    }
}
