package net.creeperhost.polylib.inventory.items;

import net.creeperhost.polylib.Serializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Created by brandon3055 on 04/04/2024
 */
public class BlockInventory implements Container, Serializable, ContainerHelpers {

    private final BlockEntity blockEntity;

    private final Predicate<Player> canPlayerUse = player -> getBlockEntity().getBlockPos().distSqr(player.blockPosition()) <= 64;
    private BiPredicate<Integer, ItemStack> stackValidator = null;
    private Map<Integer, Predicate<ItemStack>> slotValidators = new HashMap<>();
    private final NonNullList<ItemStack> items;
    private int maxStackSize = 64;

    public BlockInventory(BlockEntity blockEntity, int size) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.blockEntity = blockEntity;
    }

    public BlockInventory setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public BlockInventory setStackValidator(BiPredicate<Integer, ItemStack> stackValidator) {
        this.stackValidator = stackValidator;
        return this;
    }

    public BlockInventory setStackValidator(Predicate<ItemStack> stackValidator) {
        this.stackValidator = (integer, stack) -> stackValidator.test(stack);
        return this;
    }

    public BlockInventory setSlotValidator(int slot, Predicate<ItemStack> validator) {
        slotValidators.put(slot, validator);
        return this;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return items.get(i);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(items, i);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return canPlayerUse.test(player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public void deserialize(HolderLookup.Provider provider, CompoundTag compoundTag) {
        ContainerHelper.loadAllItems(compoundTag, items, provider);
    }

    @Override
    public CompoundTag serialize(HolderLookup.Provider provider, CompoundTag compoundTag) {
        return ContainerHelper.saveAllItems(compoundTag, items, provider);
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slotValidators.containsKey(slot)) {
            return slotValidators.get(slot).test(stack);
        }
        return stackValidator == null || stackValidator.test(slot, stack);
    }
}
