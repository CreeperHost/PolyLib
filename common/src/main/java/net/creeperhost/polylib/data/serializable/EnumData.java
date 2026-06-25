package net.creeperhost.polylib.data.serializable;

import net.creeperhost.polylib.helpers.MathUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link AbstractDataStore} implementation for enum values.
 * <p>
 * Values are encoded to tags by enum ordinal index and to network buffers with
 * Minecraft's enum codec helpers. Enum types with more than 255 constants are not supported.
 *
 * @param <T> the enum type stored by this data object
 */
public class EnumData<T extends Enum<T>> extends AbstractDataStore<T> {
    private final Class<T> enumClass;
    public Map<Integer, T> indexToValue = new HashMap<>();
    public Map<T, Integer> valueToIndex = new HashMap<>();

    /**
     * Creates an enum data store using the declaring class of the default value.
     *
     * @param defaultValue the initial value, used to infer the enum class
     */
    public EnumData(@NotNull T defaultValue) {
        this(defaultValue.getDeclaringClass(), defaultValue);
    }

    /**
     * Creates an enum data store.
     *
     * @param enumClass    the enum class this data store accepts
     * @param defaultValue the initial value, or null
     */
    public EnumData(Class<T> enumClass, @Nullable T defaultValue) {
        super(defaultValue);
        this.enumClass = enumClass;
        T[] v = enumClass.getEnumConstants();
        if (v.length > 255) {
            throw new RuntimeException("Max enum size supported by EnumData is 255");
        }
        for (int i = 0; i < v.length; i++) {
            this.indexToValue.put(i, v[i]);
            this.valueToIndex.put(v[i], i);
        }
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(value == null);
        if (value != null) {
            buf.writeEnum(value);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            value = validValue(buf.readEnum(enumClass), value);
        }
    }

    @Override
    public void toTag(ValueOutput output) {
        if (value != null) {
            output.putByte("value", valueToIndex.get(value).byteValue());
        } else {
            output.putBoolean("null", true);
        }
    }

    @Override
    public void fromTag(ValueInput input) {
        if (!input.getBooleanOr("null", false)) {
            value = validValue(indexToValue.get(MathUtil.clamp(input.getByteOr("value", (byte) 0) & 0xFF, 0, indexToValue.size() - 1)), value);
        }
    }

    /**
     * @return true when the stored enum value is null
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * @return true when the stored enum value is not null
     */
    public boolean notNull() {
        return value != null;
    }
}
