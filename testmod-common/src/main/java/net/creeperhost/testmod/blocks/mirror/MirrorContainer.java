package net.creeperhost.testmod.blocks.mirror;

import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.inventory.PlayerInventoryMirrorContainer;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Demonstrates {@link PlayerInventoryMirrorContainer} — all 41 vanilla player slots
 * (main 0-35, armor 36-39, offhand 40) backed by the mirror container.
 */
public class MirrorContainer extends ModularGuiContainerMenu
{
    public final PlayerInventoryMirrorContainer mirror;

    public final SlotGroup main    = createSlotGroup(0, 1);
    public final SlotGroup hotBar  = createSlotGroup(0, 1);
    public final SlotGroup armor   = createSlotGroup(1, 0);
    public final SlotGroup offhand = createSlotGroup(2, 0);

    public MirrorContainer(int id, Inventory playerInv, RegistryFriendlyByteBuf buf)
    {
        this(id, playerInv);
    }

    public MirrorContainer(int id, Inventory playerInv)
    {
        super(TestContainers.MIRROR_CONTAINER.get(), id, playerInv);
        this.mirror = new PlayerInventoryMirrorContainer(playerInv.player);

        main.addSlots(27, 9, index -> new PolySlot(mirror, index));
        hotBar.addSlots(9, 0, index -> new PolySlot(mirror, index));
        armor.addSlots(4, 36, index -> new PolySlot(mirror, index));
        offhand.addSlots(1, 40, index -> new PolySlot(mirror, index));
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }
}
