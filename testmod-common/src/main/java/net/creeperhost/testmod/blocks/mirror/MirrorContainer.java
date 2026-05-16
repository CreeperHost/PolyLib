package net.creeperhost.testmod.blocks.mirror;

import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.inventory.PlayerInventoryMirrorContainer;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Demonstrates {@link PlayerInventoryMirrorContainer} — all 41 vanilla player slots
 * (main 0-35, armor 36-39, offhand 40) backed by the mirror container.
 *
 * <p>Slot mapping inside the mirror container:
 * <ul>
 *   <li>0-8   hotbar</li>
 *   <li>9-35  main inventory</li>
 *   <li>36-39 armor (feet→head order)</li>
 *   <li>40    offhand</li>
 * </ul>
 * Because the mirror uses the same index layout as {@link net.minecraft.world.entity.player.Inventory},
 * we can create {@link PolySlot}s backed by the mirror using the same indices.
 */
public class MirrorContainer extends ModularGuiContainerMenu
{
    /** The unified Container view of all player slots. */
    public final PlayerInventoryMirrorContainer mirror;

    // Slot groups mirror the layout of ContainerInventoryTest but backed by the mirror, not Inventory directly.
    public final SlotGroup main    = createSlotGroup(0, 1);
    public final SlotGroup hotBar  = createSlotGroup(0, 1);
    public final SlotGroup armor   = createSlotGroup(1, 0);
    public final SlotGroup offhand = createSlotGroup(2, 0);

    public MirrorContainer(int id, Inventory playerInv)
    {
        super(TestContainers.MIRROR_CONTAINER.get(), id, playerInv);
        this.mirror = new PlayerInventoryMirrorContainer(playerInv.player);

        // Main 27 slots (indices 9-35)
        main.addSlots(27, 9, index -> new PolySlot(mirror, index));
        // Hotbar 9 slots (indices 0-8)
        hotBar.addSlots(9, 0, index -> new PolySlot(mirror, index));
        // Armor 4 slots (indices 36-39)
        armor.addSlots(4, 36, index -> new PolySlot(mirror, index));
        // Offhand 1 slot (index 40)
        offhand.addSlots(1, 40, index -> new PolySlot(mirror, index));
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }
}
