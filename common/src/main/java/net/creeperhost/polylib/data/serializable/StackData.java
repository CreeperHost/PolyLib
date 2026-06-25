package net.creeperhost.polylib.data.serializable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * {@link AbstractDataStore} implementation for an {@link ItemStack}.
 * <p>
 * Stored stacks are copied on assignment, and equality is checked with
 * {@link ItemStack#matches(ItemStack, ItemStack)} so stack components are compared
 * using Minecraft's item stack matching rules.
 */
public class StackData extends AbstractDataStore<ItemStack> {

    /**
     * Creates an item stack data store with {@link ItemStack#EMPTY}.
     */
    public StackData() {
        super(ItemStack.EMPTY);
    }

    /**
     * Creates an item stack data store.
     *
     * @param defaultValue the initial stack
     */
    public StackData(ItemStack defaultValue) {
        super(defaultValue);
    }

    /**
     * Stores a copy of the supplied stack when it differs from the current stack and passes validation.
     *
     * @param value the requested new stack
     * @return the stack currently stored after validation
     */
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
