package net.creeperhost.polylib.inventory.items;

import net.creeperhost.polylib.util.Serializable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Simple {@link Container} implementation for inventories owned by a {@link BlockEntity}.
 * <p>
 * This inventory stores its contents in a fixed-size {@link NonNullList}, marks the
 * owning block entity dirty whenever its contents change, and serializes its items
 * through {@link ContainerHelper}. It also supports a global stack validator and
 * per-slot validators for menu and automation checks.
 */
public class BlockInventory implements Container, Serializable, ContainerHelpers {

    private final BlockEntity blockEntity;

    private final Predicate<Player> canPlayerUse = player -> getBlockEntity().getBlockPos().distSqr(player.blockPosition()) <= 64;
    private BiPredicate<Integer, ItemStack> stackValidator = null;
    private Map<Integer, Predicate<ItemStack>> slotValidators = new HashMap<>();
    private final NonNullList<ItemStack> items;
    private int maxStackSize = 64;

    /**
     * Creates a block inventory with the supplied slot count.
     *
     * @param blockEntity the block entity that owns this inventory
     * @param size        the number of inventory slots
     */
    public BlockInventory(BlockEntity blockEntity, int size) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.blockEntity = blockEntity;
    }

    /**
     * Sets the maximum stack size accepted by this inventory.
     *
     * @param maxStackSize the maximum number of items allowed in one slot
     * @return this inventory
     */
    public BlockInventory setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    /**
     * Sets a global validator used by {@link #canPlaceItem(int, ItemStack)} when
     * no per-slot validator is registered for the target slot.
     *
     * @param stackValidator predicate receiving the slot index and stack
     * @return this inventory
     */
    public BlockInventory setStackValidator(BiPredicate<Integer, ItemStack> stackValidator) {
        this.stackValidator = stackValidator;
        return this;
    }

    /**
     * Sets a global stack-only validator used by {@link #canPlaceItem(int, ItemStack)}
     * when no per-slot validator is registered for the target slot.
     *
     * @param stackValidator predicate receiving the stack
     * @return this inventory
     */
    public BlockInventory setStackValidator(Predicate<ItemStack> stackValidator) {
        this.stackValidator = (integer, stack) -> stackValidator.test(stack);
        return this;
    }

    /**
     * Sets a validator for a specific slot.
     * <p>
     * Slot validators take priority over the global stack validator.
     *
     * @param slot      the slot index to validate
     * @param validator predicate receiving the stack being inserted
     * @return this inventory
     */
    public BlockInventory setSlotValidator(int slot, Predicate<ItemStack> validator) {
        slotValidators.put(slot, validator);
        return this;
    }

    /**
     * @return the block entity that owns this inventory
     */
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
    public void deserialize(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    public void serialize(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
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
