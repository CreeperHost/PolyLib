package net.creeperhost.polylib.data.serializable;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

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
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = validValue(buf.readBoolean(), value);
    }

    @Override
    public Tag toTag() {
        return ByteTag.valueOf(value);
    }

    @Override
    public void fromTag(Tag tag) {
        value = validValue(((NumericTag) tag).getAsByte() != 0, value);
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
