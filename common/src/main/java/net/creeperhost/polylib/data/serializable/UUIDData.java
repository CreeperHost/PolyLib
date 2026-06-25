package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * {@link AbstractDataStore} wrapping a nullable {@link UUID} value.
 * <p>
 * NBT: stored as a string ({@code ""} represents {@code null}).
 * Network: presence flag (boolean) followed by two longs (most/least significant bits).
 * <p>
 * Created for DisCraftHonored PolyLib integration.
 */
public class UUIDData extends AbstractDataStore<@Nullable UUID> {

    /**
     * Creates a UUID data store with a null default value.
     */
    public UUIDData() {
        super(null);
    }

    /**
     * Creates a UUID data store.
     *
     * @param defaultValue the initial value, or null
     */
    public UUIDData(@Nullable UUID defaultValue) {
        super(defaultValue);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(value != null);
        if (value != null) {
            buf.writeLong(value.getMostSignificantBits());
            buf.writeLong(value.getLeastSignificantBits());
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        boolean present = buf.readBoolean();
        UUID read = present ? new UUID(buf.readLong(), buf.readLong()) : null;
        value = validValue(read, value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.putString("value", value != null ? value.toString() : "");
    }

    @Override
    public void fromTag(ValueInput input) {
        String s = input.getStringOr("value", "");
        if (s.isEmpty()) {
            value = null;
        } else {
            try {
                value = UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                value = null;
            }
        }
    }
}
