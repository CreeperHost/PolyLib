package net.creeperhost.polylib.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.*;

/**
 * A fully configurable inventory slot.
 * If there is anything this slot can not do... Let me know.
 * <p>
 * Created by brandon3055 on 10/09/2023
 */
public class PolySlot extends Slot {
    private boolean canPlace = true;
    private boolean checkContainer = true;
    private Supplier<Boolean> enabled = () -> true;
    private Predicate<ItemStack> validator = stack -> true;
    private Function<ItemStack, Integer> stackLimit = stack -> Integer.MAX_VALUE;
    private BiPredicate<Player, ItemStack> canRemove = (player, stack) -> true;
    private BiConsumer<ItemStack, ItemStack> onSetByPlayer = (oldStack, newStack) -> {
    };

    public PolySlot(Container container, int index) {
        this(container, index, 0, 0);
    }

    public PolySlot(Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    /**
     * Configure this slot as an output only slot.
     * Items can not be placed in this slot by the player.
     */
    public PolySlot output() {
        canPlace = false;
        return this;
    }

    /**
     * Do not use the containers canPlaceItem when checking if an item can be placed.
     */
    public PolySlot noCheck() {
        checkContainer = false;
        return this;
    }

    /**
     * Allows you to attach a validator to control what items are allowed in this slot.
     * You can also limit a slots allowed contents via the {@link Container#canPlaceItem(int, ItemStack)} method of the container.
     *
     * @param validator The validator predicate, If the predicate returns false for a stack, the stack will not be placed.
     */
    public PolySlot setValidator(Predicate<ItemStack> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Allows you to get a callback when the player sets this slots contents.
     * Parameters given are Old stack then New stack.
     */
    public PolySlot onSetByPlayer(BiConsumer<ItemStack, ItemStack> onSetByPlayer) {
        this.onSetByPlayer = onSetByPlayer;
        return this;
    }

    /**
     * Allows you to apply a stack size limit that (if smaller) will override the container and the item stack limits.
     */
    public PolySlot setStackLimit(Function<ItemStack, Integer> stackLimit) {
        this.stackLimit = stackLimit;
        return this;
    }

    /**
     * Allows you to attach a "can remove" predicate that can block removal of a stack from the slot by the player.
     */
    public PolySlot setCanRemove(BiPredicate<Player, ItemStack> canRemove) {
        this.canRemove = canRemove;
        return this;
    }

    public PolySlot setEnabled(Supplier<Boolean> enabled) {
        this.enabled = enabled;
        return this;
    }

    public PolySlot setEnabled(boolean enabled) {
        this.enabled = () -> enabled;
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return canPlace && validator.test(itemStack) && (!checkContainer || container.canPlaceItem(getContainerSlot(), itemStack));
    }

    @Override
    public boolean mayPickup(Player player) {
        return canRemove.test(player, getItem());
    }

    @Override
    public void setByPlayer(ItemStack itemStack) {
        onSetByPlayer.accept(getItem(), itemStack);
        super.setByPlayer(itemStack);
    }

    @Override
    public boolean isActive() {
        return enabled.get();
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        return Math.min(super.getMaxStackSize(itemStack), stackLimit.apply(itemStack));
    }
}
