package net.creeperhost.polylib.data.serializable;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class FluidData extends AbstractDataStore<FluidStack> {

    public FluidData() {
        super(FluidStack.empty());
    }

    public FluidData(FluidStack defaultValue) {
        super(defaultValue);
    }

    @Override
    public void setValue(FluidStack value) {
        this.value = value.copy();
        markDirty();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        value.write(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = FluidStack.read(buf);
    }

    @Override
    public Tag toTag() {
        return value.write(new CompoundTag());
    }

    @Override
    public void fromTag(Tag tag) {
        value = FluidStack.read((CompoundTag) tag);
    }

    @Override
    public boolean isSameValue(FluidStack newValue) {
        return value.equals(newValue);
    }
}
