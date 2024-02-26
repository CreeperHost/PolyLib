package net.creeperhost.polylib.data.serializable;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

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
    public FluidStack set(FluidStack value) {
        if (!Objects.equals(value, this.value) && validator.test(value)) {
            this.value = value.copy();
            markDirty();
        }
        return this.value;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        value.write(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = validValue(FluidStack.read(buf), value);
    }

    @Override
    public Tag toTag() {
        return value.write(new CompoundTag());
    }

    @Override
    public void fromTag(Tag tag) {
        value = validValue(FluidStack.read((CompoundTag) tag), value);
    }

    @Override
    public boolean isSameValue(FluidStack newValue) {
        return value.equals(newValue);
    }
}
