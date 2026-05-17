package net.creeperhost.polylib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A live Container view of a ServerPlayer's inventory (main + armor + offhand) plus
 * any Curios slots appended by the platform layer.
 *
 * Slot layout (matches VoidAnomalyEntity.SNAPSHOT_SIZE = 41):
 *   0-35   main inventory (including hotbar 0-8)
 *   36-39  armor slots (feet, legs, chest, head)
 *   40     offhand
 *   41+    curios slots (appended by CuriosPlayerInventoryMirror if Curios is present)
 *
 * This container does NOT copy items — every read/write goes directly to the player's
 * live Inventory, so changes are immediately visible to the player without any sync step.
 */
public class PlayerInventoryMirrorContainer implements Container {

    /** Extra curios-slot appender; set by platform layer when Curios is loaded. */
    public interface CuriosSlotProvider {
        int getCuriosSlotCount(Player player);
        ItemStack getCuriosSlot(Player player, int curiosIndex);
        void setCuriosSlot(Player player, int curiosIndex, ItemStack stack);
    }

    private static CuriosSlotProvider curiosSlotProvider = null;

    public static void setCuriosSlotProvider(CuriosSlotProvider provider) {
        curiosSlotProvider = provider;
    }

    // ── Slot-count constants (vanilla layout) ─────────────────────────────────
    private static final int MAIN_SLOTS   = 36; // 0-35
    private static final int ARMOR_SLOTS  = 4;  // 36-39
    private static final int OFFHAND_SLOT = 1;  // 40
    private static final int VANILLA_SLOTS = MAIN_SLOTS + ARMOR_SLOTS + OFFHAND_SLOT; // 41

    private final Player player;

    public PlayerInventoryMirrorContainer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    private int curiosSlotCount() {
        return curiosSlotProvider != null ? curiosSlotProvider.getCuriosSlotCount(player) : 0;
    }

    /** The real number of backing slots (vanilla + curios). */
    private int realSize() {
        return VANILLA_SLOTS + curiosSlotCount();
    }

    /**
     * Returns the container size rounded up to the nearest multiple of 9, minimum 54 (6 chest rows).
     * Padding slots (realSize()..getContainerSize()-1) are read-only empty and reject placement.
     */
    @Override
    public int getContainerSize() {
        int raw = realSize();
        int rounded = ((raw + 8) / 9) * 9;
        return Math.max(54, rounded);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 0 && slot < realSize();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        if (slot < VANILLA_SLOTS) {
            // Inventory.getItem routes slots 0-35 to items list,
            // 36-39 to armor via EQUIPMENT_SLOT_MAPPING, and 40 to offhand.
            return player.getInventory().getItem(slot);
        } else {
            int curiosIndex = slot - VANILLA_SLOTS;
            if (curiosSlotProvider != null) {
                return curiosSlotProvider.getCuriosSlot(player, curiosIndex);
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        ItemStack current = getItem(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemStack split = current.split(amount);
        setChanged();
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        ItemStack current = getItem(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        setItem(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= getContainerSize()) return;
        if (slot < VANILLA_SLOTS) {
            player.getInventory().setItem(slot, stack);
        } else {
            int curiosIndex = slot - VANILLA_SLOTS;
            if (curiosSlotProvider != null) {
                curiosSlotProvider.setCuriosSlot(player, curiosIndex, stack);
            }
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        player.getInventory().setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        // Any server-side automation can interact; GUI access is controlled at the menu level
        return true;
    }

    @Override
    public void clearContent() {
        int size = getContainerSize();
        for (int i = 0; i < size; i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }
}
