package net.creeperhost.polylib.containers;

import net.creeperhost.polylib.client.modulargui.elements.GuiSlots;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The base abstract ContainerMenu for all modular gui containers.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public abstract class ModularGuiContainerMenu extends AbstractContainerMenu {

    public final Inventory inventory;
    public final List<SlotGroup> slotGroups = new ArrayList<>();
    public final Map<Slot, SlotGroup> slotGroupMap = new HashMap<>();
    public final Map<Integer, List<Slot>> zonedSlots = new HashMap<>();
    public final List<DataSync<?>> dataSyncs = new ArrayList<>();
    private BiConsumer<ServerPlayer, Consumer<FriendlyByteBuf>> serverToClientPacketHandler;
    private Consumer<Consumer<FriendlyByteBuf>> clientToServerPacketHandler;

    protected ModularGuiContainerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory) {
        super(menuType, containerId);
        this.inventory = inventory;
    }

    /**
     * Creates and returns a new slot group for this container.
     * You can then add your inventory slots to this slot group, similar to how you would normally add slots to the container.
     * With one big exception! You do not need to worry about setting slot positions! (Just use 0, 0)
     * <p>
     * Make sure to save your slot groups to accessible fields in your container menu class.
     * You will need to pass these to appropriate {@link GuiSlots} / {@link GuiSlots#singleSlot(GuiParent, ContainerScreenAccess, SlotGroup, int)} elements.
     * The gui elements will handle positioning and rendering the slots.
     * <p>
     * As far as splitting a containers slots into multiple groups, Typically the players main inventory and hot bar would be added as two separate groups.
     * How you handle the containers slots is up to you, For something like a machine with several spread out slots,
     * you can still add all the slots to a single group, then pass each individual slot from the group to a single {@link GuiSlots#singleSlot(GuiParent, ContainerScreenAccess, SlotGroup, int)} element.
     *
     * @param zoneId Slot zoneId used to control quick-move. Quick move will always attempt to move to the next zoneId with space,
     *               If moving from the highest zoneId, Will wrap around to the lowest zoneId.
     *               zoneId must be >= 0, or you can use -1 to disable quick move into a zone.
     */
    //TODO, Implement zone-based quick move.
    protected SlotGroup createSlotGroup(int zoneId) {
        SlotGroup group = new SlotGroup(this, zoneId);
        slotGroups.add(group);
        return group;
    }

    /**
     * Convenience method to create a slot group for player slots. (zoneId 0)
     *
     * @see #createSlotGroup(int)
     */
    protected SlotGroup playerSlotGroup() {
        return createSlotGroup(0);
    }

    /**
     * Convenience method to create a slot group for the 'other side' of the inventory
     * So the Block/tile or whatever this inventory is attached to. (zoneId 1)
     *
     * @see #createSlotGroup(int)
     */
    protected SlotGroup remoteSlotGroup() {
        return createSlotGroup(1);
    }

    //=== Network ===//

    /**
     * Set the server to client packet handler.
     * As polylib does not have its own network implementation, the implementor of ModularGuiContainerMenu must provide their own if
     * they wish to use the network functionality built into ModularGuiContainerMenu.
     * <p>
     * An example imeplementation may look something like:
     * <pre>
     * setServerToClientPacketHandler((player, packetWriter) -> {
     *     FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
     *     packetWriter.accept(buf);
     *     MyNetwork.sendModularGuiMenuPacketToPlayer(player, buf);
     * });
     * </pre>
     * Then, in your client side packet handler you would call {@link ModularGuiContainerMenu#handlePacketFromServer(Player, FriendlyByteBuf)}
     * The player can be the client side player.
     *
     * <p>
     * A server to client packet handler is required for the {@link DataSync} system to work.
     */
    public void setServerToClientPacketHandler(BiConsumer<ServerPlayer, Consumer<FriendlyByteBuf>> serverToClientPacketHandler) {
        this.serverToClientPacketHandler = serverToClientPacketHandler;
    }

    /**
     * This should be implemented similar to {@link #setServerToClientPacketHandler(BiConsumer)}
     * The difference being this will be sending packets in the other direction.
     */
    public void setClientToServerPacketHandler(Consumer<Consumer<FriendlyByteBuf>> clientToServerPacketHandler) {
        this.clientToServerPacketHandler = clientToServerPacketHandler;
    }

    /**
     * Send a packet to the client side container.
     * Requires a server to client packet handler to be installed via {@link #setServerToClientPacketHandler(BiConsumer)}
     *
     * @param packetId     message id, Can be any value from 0 to 254, 255 is used by the {@link DataSync} system.
     * @param packetWriter Use this callback to write your data to the packet.
     */
    public void sendPacketToClient(int packetId, Consumer<FriendlyByteBuf> packetWriter) {
        if (serverToClientPacketHandler != null && inventory.player instanceof ServerPlayer serverPlayer) {
            serverToClientPacketHandler.accept(serverPlayer, buf -> {
                buf.writeByte(containerId);
                buf.writeByte((byte) packetId);
                packetWriter.accept(buf);
            });
        }
    }

    /**
     * Send a packet to the server side container.
     * Requires a client to server packet handler to be installed via {@link #setClientToServerPacketHandler(Consumer)}
     *
     * @param packetId     message id, Can be any value from 0 to 255
     * @param packetWriter Use this callback to write your data to the packet.
     */
    public void sendPacketToServer(int packetId, Consumer<FriendlyByteBuf> packetWriter) {
        if (clientToServerPacketHandler != null) {
            clientToServerPacketHandler.accept(buf -> {
                buf.writeByte(containerId);
                buf.writeByte((byte) packetId);
                packetWriter.accept(buf);
            });
        }
    }

    public static void handlePacketFromClient(Player player, FriendlyByteBuf packet) {
        int containerId = packet.readByte();
        int packetId = packet.readByte() & 0xFF;
        if (player.containerMenu instanceof ModularGuiContainerMenu menu && menu.containerId == containerId) {
            menu.handlePacketFromClient(player, packetId, packet);
        }
    }

    /**
     * Override this in your container menu implementation in order to receive packets sent via {@link #sendPacketToServer(int, Consumer)}
     * Requires a client to server packet handler to be installed via {@link #setClientToServerPacketHandler(Consumer)}
     */
    public void handlePacketFromClient(Player player, int packetId, FriendlyByteBuf packet) {

    }

    public static void handlePacketFromServer(Player player, FriendlyByteBuf packet) {
        int containerId = packet.readByte();
        int packetId = packet.readByte() & 0xFF;
        if (player.containerMenu instanceof ModularGuiContainerMenu menu && menu.containerId == containerId) {
            menu.handlePacketFromServer(player, packetId, packet);
        }
    }

    /**
     * Override this in your container menu implementation in order to receive packets sent via {@link #sendPacketToServer(int, Consumer)}
     * Requires a server to client packet handler to be installed via {@link #setServerToClientPacketHandler(BiConsumer)}
     * <p>
     * Don't forget to call super if you plan on using the {@link DataSync} system.
     */
    public void handlePacketFromServer(Player player, int packetId, FriendlyByteBuf packet) {
        if (packetId == 255) {
            int index = packet.readByte() & 0xFF;
            if (dataSyncs.size() > index) {
                dataSyncs.get(index).handleSyncPacket(packet);
            }
        }
    }

    //=== Quick Move ===///

    /**
     * Determines if two @link {@link ItemStack} match and can be merged into a single slot
     */
    public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) return false;
        return ItemStack.matches(stack1, stack2);
    }

    /**
     * Logic to figure out where/if a shift-clicked slot can move its @link {@link ItemStack} to another @link {@link Slot}
     */
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        int numSlots = slots.size();
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            originalStack = stackInSlot.copy();
            if (slotIndex < numSlots - 9 * 4 || !tryShiftItem(stackInSlot, numSlots)) {
                if (slotIndex >= numSlots - 9 * 4 && slotIndex < numSlots - 9) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9, numSlots)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= numSlots - 9 && slotIndex < numSlots) {
                    if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots - 9)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!shiftItemStack(stackInSlot, numSlots - 9 * 4, numSlots)) {
                    return ItemStack.EMPTY;
                }
            }

            slot.onQuickCraft(stackInSlot, originalStack);
            if (stackInSlot.getCount() <= 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return originalStack;
    }

    public boolean shiftItemStack(ItemStack stackToShift, int start, int end) {
        boolean changed = false;
        if (stackToShift.isStackable()) {
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
                Slot slot = slots.get(slotIndex);
                ItemStack stackInSlot = slot.getItem();
                if (!stackInSlot.isEmpty() && canStacksMerge(stackInSlot, stackToShift)) {
                    int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getMaxStackSize());
                    if (resultingStackSize <= max) {
                        stackToShift.setCount(0);
                        stackInSlot.setCount(resultingStackSize);
                        slot.setChanged();
                        changed = true;
                    } else if (stackInSlot.getCount() < max) {
                        stackToShift.setCount(stackToShift.getCount() - (max - stackInSlot.getCount()));
                        stackInSlot.setCount(max);
                        slot.setChanged();
                        changed = true;
                    }
                }
            }
        }
        if (stackToShift.getCount() > 0) {
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
                Slot slot = slots.get(slotIndex);
                ItemStack stackInSlot = slot.getItem();
                if (stackInSlot.isEmpty()) {
                    int max = Math.min(stackToShift.getMaxStackSize(), slot.getMaxStackSize());
                    stackInSlot = stackToShift.copy();
                    stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
                    stackToShift.setCount(stackToShift.getCount() - stackInSlot.getCount());
                    slot.set(stackInSlot);
                    slot.setChanged();
                    changed = true;
                }
            }
        }
        return changed;
    }

    public boolean tryShiftItem(ItemStack stackToShift, int numSlots) {
        for (int machineIndex = 0; machineIndex < numSlots - 9 * 4; machineIndex++) {
            Slot slot = slots.get(machineIndex);
            if (!slot.mayPlace(stackToShift)) continue;
            if (shiftItemStack(stackToShift, machineIndex, machineIndex + 1)) return true;
        }
        return false;
    }


    //=== Internal Methods ===//

    public void mapSlot(Slot slot, SlotGroup slotGroup) {
        slotGroupMap.put(slot, slotGroup);
        zonedSlots.computeIfAbsent(slotGroup.zone, e -> new ArrayList<>()).add(slot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        dataSyncs.forEach(DataSync::detectAndSend);
    }
}
