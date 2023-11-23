package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.PolyContainer;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.data.serializable.ByteData;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.network.TestNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ContainerInventoryTestBlock extends ModularGuiContainerMenu
{
    public final InventoryTestBlockEntity blockEntity;
    public final SlotGroup main = createSlotGroup(0, 1, 3); //zone id is 0, Quick move to zone 1, then 3
    public final SlotGroup hotBar = createSlotGroup(0, 1, 3);
    public final SlotGroup armor = createSlotGroup(1, 3, 0); //zone id is 1, Quick move to zone 3, then 0
    public final SlotGroup offhand = createSlotGroup(2, 3, 0);

    public final SlotGroup machineInputs = createSlotGroup(3, 1, 0, 2);//zone id is 3, Quick move to zone 1, then 0, then 2
    public final SlotGroup machineOutputs = createSlotGroup(3, 1, 0, 2);

    public final DataSync<Byte> progressSync;

    public ContainerInventoryTestBlock(int id, Inventory playerInv, FriendlyByteBuf extraData)
    {
        this(id, playerInv, (InventoryTestBlockEntity) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerInventoryTestBlock(int id, Inventory playerInv, InventoryTestBlockEntity inventoryTestBlock)
    {
        super(TestContainers.TEST_INVENTORY_CONTAINER.get(), id, playerInv);
        this.blockEntity = inventoryTestBlock;
        setServerToClientPacketHandler(TestNetwork::sendContainerPacketToClient);
        setClientToServerPacketHandler(TestNetwork::sendContainerPacketToServer);

        progressSync = new DataSync<>(this, new ByteData(), () -> (byte) blockEntity.progress);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);

        armor.addPlayerArmor(inventory);
        offhand.addPlayerOffhand(inventory);

        machineInputs.addSlots(1, 0, index -> new PolySlot(blockEntity.getContainer(), index));
        machineOutputs.addAllSlots(blockEntity.getOutputContainer(), (container, integer) -> new PolySlot(container, integer).output());
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }
}
