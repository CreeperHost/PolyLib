package net.creeperhost.testmod.blocks.mguitestblock;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.data.serializable.ByteData;
import net.creeperhost.polylib.data.serializable.FluidData;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.network.TestNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class MGuiTestBlockContainerMenu extends ModularGuiContainerMenu {
    public final MGuiTestBlockEntity blockEntity;
    public final DataSync<Byte> progressSync;
    public final DataSync<Integer> energy;
    public final DataSync<Integer> maxEnergy;
    public final DataSync<Integer> tankCap;
    public final DataSync<FluidStack> waterTank;
    public final DataSync<FluidStack> lavaTank;

    public final SlotGroup main = playerSlotGroup();
    public final SlotGroup hotBar = playerSlotGroup();
    public final SlotGroup armor = playerSlotGroup();
    public final SlotGroup offhand = playerSlotGroup();
    public final SlotGroup machineInputs = remoteSlotGroup();
    public final SlotGroup machineOutputs = remoteSlotGroup();

    public MGuiTestBlockContainerMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, (MGuiTestBlockEntity) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()));
    }

    public MGuiTestBlockContainerMenu(int containerId, Inventory inventory, MGuiTestBlockEntity blockEntity) {
        super(TestContainers.MGUI_TEST_BLOCK_CONTAINER.get(), containerId, inventory);
        this.blockEntity = blockEntity;

        setServerToClientPacketHandler(TestNetwork::sendContainerPacketToClient);
        setClientToServerPacketHandler(TestNetwork::sendContainerPacketToServer);

        progressSync = new DataSync<>(this, new ByteData(), () -> (byte) blockEntity.progress);
        energy = new DataSync<>(this, new IntData(), () -> blockEntity.energy);
        maxEnergy = new DataSync<>(this, new IntData(), () -> blockEntity.maxEnergy);
        tankCap = new DataSync<>(this, new IntData(), () -> blockEntity.tankCapacity);
        waterTank = new DataSync<>(this, new FluidData(), () -> blockEntity.waterStorage);
        lavaTank = new DataSync<>(this, new FluidData(), () -> blockEntity.lavaStorage);

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);
        armor.addPlayerArmor(inventory);
        offhand.addPlayerOffhand(inventory);
        machineInputs.addSlots(3, 0, index -> new PolySlot(blockEntity.inventory, index)
                .setValidator(stack ->
                        switch (index) {
                            case 0 -> stack.is(Items.REDSTONE);
                            case 1 -> stack.is(Items.WATER_BUCKET);
                            default -> stack.is(Items.LAVA_BUCKET);
                        }
                ));
        machineOutputs.addAllSlots(blockEntity.outputInv, (container, integer) -> new PolySlot(container, integer).output());
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
