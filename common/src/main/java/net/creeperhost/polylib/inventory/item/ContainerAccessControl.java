package net.creeperhost.polylib.inventory.item;

import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Used to provide control over what slots allow insert / extract / under what conditions etc.
 * Designed for use with {@link ItemInventoryBlock} but will work with anything that accepts a {@link WorldlyContainer}
 * <p>
 * When implementing you can return a single instance for all faces,
 * or you can return different instances for different faces if you need custom per-face configurations.
 * <p>
 * Created by brandon3055 on 08/03/2024
 */
@Deprecated //Moved to items package
public class ContainerAccessControl implements SerializableContainer, WorldlyContainer {
    private final IntObjectHashMap<Predicate<ItemStack>> slotInsertChecks = new IntObjectHashMap<>();
    private final IntObjectHashMap<Predicate<ItemStack>> slotRemoveChecks = new IntObjectHashMap<>();
    private BiPredicate<Integer, ItemStack> containerInsertCheck = (slot, stack) -> true;
    private BiPredicate<Integer, ItemStack> containerRemoveCheck = (slot, stack) -> true;
    private final Container wrapped;
    private final int[] slots;

    public ContainerAccessControl(Container wrapped, int... slots) {
        this.wrapped = wrapped;
        this.slots = slots;
    }

    /**
     * @param firstSlot First slot index (inclusive)
     * @param lastSlot Last slot index (exclusive)
     */
    public ContainerAccessControl(Container wrapped, int firstSlot, int lastSlot) {
        this.wrapped = wrapped;
        this.slots = new int[lastSlot - firstSlot];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = firstSlot + i;
        }
    }

    /**
     * Allows you to define a container-wide predicate that can allow or block the insertion of items,
     * This check will be ignored if there is a per-slot predicate for the slot in question.
     * The given integer will be the slot index within the wrapped container.
     */
    public ContainerAccessControl containerInsertCheck(BiPredicate<Integer, ItemStack> containerInsertCheck) {
        this.containerInsertCheck = containerInsertCheck;
        return this;
    }

    /**
     * Allows you to define a container-wide predicate that can allow or block the removal of items,
     * This check will be ignored if there is a per-slot predicate for the slot in question.
     * The given integer will be the slot index within the wrapped container.
     */
    public ContainerAccessControl containerRemoveCheck(BiPredicate<Integer, ItemStack> containerRemoveCheck) {
        this.containerRemoveCheck = containerRemoveCheck;
        return this;
    }

    /**
     * Allows you to define a per-slot predicate that can allow or block the insertion of items,
     *
     * @param slot            The slot index within the wrapped container.
     * @param slotInsertCheck A predicate that can allow or deny insertion of the slot contents.
     */
    public ContainerAccessControl slotInsertCheck(int slot, Predicate<ItemStack> slotInsertCheck) {
        this.slotInsertChecks.put(slot, slotInsertCheck);
        return this;
    }

    /**
     * Allows you to define a per-slot predicate that can allow or block the removal of items,
     *
     * @param slot            The slot index within the wrapped container.
     * @param slotRemoveCheck A predicate that can allow or deny removal of the slot contents.
     */
    public ContainerAccessControl slotRemoveCheck(int slot, Predicate<ItemStack> slotRemoveCheck) {
        this.slotRemoveChecks.put(slot, slotRemoveCheck);
        return this;
    }

    //=== Access Control ===

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        if (slotInsertChecks.containsKey(i)) {
            return slotInsertChecks.get(i).test(itemStack);
        }
        return containerInsertCheck.test(i, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        if (slotRemoveChecks.containsKey(i)) {
            return slotRemoveChecks.get(i).test(itemStack);
        }
        return containerRemoveCheck.test(i, itemStack);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        if (slotInsertChecks.containsKey(i)) {
            return slotInsertChecks.get(i).test(itemStack) && wrapped.canPlaceItem(i, itemStack);
        }
        return containerInsertCheck.test(i, itemStack) && wrapped.canPlaceItem(i, itemStack);
    }

    //=== Wrapped Passthrough ===
    //@formatter:off
    @Override public int getContainerSize() { return wrapped.getContainerSize(); }
    @Override public boolean isEmpty() { return wrapped.isEmpty(); }
    @Override public ItemStack getItem(int i) { return wrapped.getItem(i); }
    @Override public ItemStack removeItem(int i, int j) { return wrapped.removeItem(i, j); }
    @Override public ItemStack removeItemNoUpdate(int i) { return wrapped.removeItemNoUpdate(i); }
    @Override public void setItem(int i, ItemStack itemStack) { wrapped.setItem(i, itemStack); }
    @Override public int getMaxStackSize() { return wrapped.getMaxStackSize(); }
    @Override public void setChanged() { wrapped.setChanged(); }
    @Override public boolean stillValid(Player player) { return wrapped.stillValid(player); }
    @Override public void clearContent() { wrapped.clearContent(); }
    @Override public void deserialize(CompoundTag nbt) { }
    @Override public CompoundTag serialize(CompoundTag nbt) { return nbt; }
    //@formatter:on
}