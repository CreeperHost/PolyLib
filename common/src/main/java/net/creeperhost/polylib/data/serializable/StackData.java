package net.creeperhost.polylib.data.serializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(value);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        value = validValue(buf.readItem(), value);
    }

    @Override
    public Tag toTag() {
        return value.save(new CompoundTag());
    }

    @Override
    public void fromTag(Tag tag) {
        value = validValue(ItemStack.of((CompoundTag) tag), value);
    }

    @Override
    public boolean isSameValue(ItemStack newValue) {
        return ItemStack.matches(value, newValue);
    }
}
