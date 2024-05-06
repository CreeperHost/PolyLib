package net.creeperhost.polylib.data.serializable;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

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
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(!value.isEmpty());
        if (!value.isEmpty()) {
            value.write(buf);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        if (buf.readBoolean()){
            value = validValue(FluidStack.read(buf), value);
        } else {
            value = FluidStack.empty();
        }
    }

    @Override
    public Tag toTag(Provider provider) {
        return value.write(provider, new CompoundTag());
    }

    @Override
    public void fromTag(Provider provider, Tag tag) {
        value = validValue(FluidStack.read(provider, tag).orElse(FluidStack.empty()), value);
    }

    @Override
    public boolean isSameValue(FluidStack newValue) {
        return value.equals(newValue);
    }
}
