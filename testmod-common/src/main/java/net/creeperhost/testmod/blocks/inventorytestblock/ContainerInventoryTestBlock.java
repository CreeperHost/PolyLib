package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.containers.PolyContainer;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.NotNull;

public class ContainerInventoryTestBlock extends PolyContainer
{
    ContainerData containerData;

    public ContainerInventoryTestBlock(int id, Inventory playerInv, FriendlyByteBuf extraData)
    {
        this(id, playerInv, (InventoryTestBlockEntity) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1));
    }

    public ContainerInventoryTestBlock(int id, Inventory playerInv, InventoryTestBlockEntity inventoryTestBlock, ContainerData containerData)
    {
        super(TestContainers.TEST_INVENTORY_CONTAINER.get(), id);

        inventoryTestBlock.getSlots().forEach(this::addSlot);

        drawPlayersInv(playerInv, 15, 132);
        drawPlayersHotBar(playerInv, 15, 132 + 58);
        this.containerData = containerData;
        addDataSlots(containerData);
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }

    public ContainerData getContainerData()
    {
        return containerData;
    }
}
