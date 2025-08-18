package net.creeperhost.polylib.data.serializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class StackData extends AbstractDataStore<ItemStack> {

    public StackData() {
        super(ItemStack.EMPTY);
    }

    public StackData(ItemStack defaultValue) {
        super(defaultValue);
    }

    @Override
    public ItemStack set(ItemStack value) {
        if (!ItemStack.matches(value, this.value) && validator.test(value)) {
            this.value = value.copy();
            markDirty();
        }
        return this.value;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.store("value", ItemStack.OPTIONAL_CODEC, value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = input.read("value", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isSameValue(ItemStack newValue) {
        return ItemStack.matches(value, newValue);
    }
}
