package net.creeperhost.polylib.data.serializable;

import net.creeperhost.polylib.helpers.MathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 19/02/2024
 */
public class EnumData<T extends Enum<T>> extends AbstractDataStore<T> {
    private final Class<T> enumClass;
    public Map<Integer, T> indexToValue = new HashMap<>();
    public Map<T, Integer> valueToIndex = new HashMap<>();

    public EnumData(@NotNull T defaultValue) {
        this(defaultValue.getDeclaringClass(), defaultValue);
    }

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
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(value == null);
        if (value != null) {
            buf.writeEnum(value);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            value = buf.readEnum(enumClass);
        }
    }

    @Override
    public Tag toTag() {
        CompoundTag nbt = new CompoundTag();
        if (value == null) {
            nbt.putBoolean("null", true);
        } else {
            nbt.putByte("value", valueToIndex.get(value).byteValue());
        }
        return nbt;
    }

    @Override
    public void fromTag(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            if (nbt.contains("null")) {
                value = null;
            } else {
                value = indexToValue.get(MathUtil.clamp(nbt.getByte("value") & 0xFF, 0, indexToValue.size() - 1));
            }
        }
    }
}
