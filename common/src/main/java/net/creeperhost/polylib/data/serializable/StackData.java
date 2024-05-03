package net.creeperhost.polylib.data.serializable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

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
    public Tag toTag(HolderLookup.Provider provider) {
        return value.saveOptional(provider);
    }

    @Override
    public void fromTag(HolderLookup.Provider provider, Tag tag) {
        value = validValue(ItemStack.parseOptional(provider, (CompoundTag) tag), value);
    }

    @Override
    public boolean isSameValue(ItemStack newValue) {
        return ItemStack.matches(value, newValue);
    }
}
