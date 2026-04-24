package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} wrapping a single {@link String} value.
 * <p>
 * NBT: stored via {@code ValueOutput.putString} / {@code ValueInput.getStringOr}.
 * Network: sent via {@code buf.writeUtf} / {@code buf.readUtf}.
 * <p>
 * Created for DisCraftHonored PolyLib integration.
 */
public class StringData extends AbstractDataStore<String> {

    public StringData() {
        super("");
    }

    public StringData(String defaultValue) {
        super(defaultValue == null ? "" : defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(buf.readUtf(), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putString("value", value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.getStringOr("value", value);
    }
}
