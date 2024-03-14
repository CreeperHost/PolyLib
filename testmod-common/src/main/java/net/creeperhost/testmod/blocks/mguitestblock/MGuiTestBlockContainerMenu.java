package net.creeperhost.testmod.blocks.mguitestblock;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.PolyBlockContainerMenu;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.data.serializable.ByteData;
import net.creeperhost.polylib.data.serializable.FluidData;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.data.serializable.LongData;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class MGuiTestBlockContainerMenu extends PolyBlockContainerMenu<MGuiTestBlockEntity> {
    public final DataSync<Byte> progressSync;
    public final DataSync<Long> energy;
    public final DataSync<FluidStack> waterTank;
    public final DataSync<FluidStack> lavaTank;

    public final SlotGroup main = createSlotGroup(0, 1, 3, 4); //zone id is 0, Quick move to zone 1, then 3
    public final SlotGroup hotBar = createSlotGroup(0, 1, 3, 4);

    public final SlotGroup armor = createSlotGroup(1, 3, 0); //zone id is 1, Quick move to zone 3, then 0
    public final SlotGroup offhand = createSlotGroup(2, 3, 0);

    public final SlotGroup machineInputs = createSlotGroup(3, 1, 0, 2);//zone id is 3, Quick move to zone 1, then 0, then 2
    public final SlotGroup machineOutputs = createSlotGroup(3, 1, 0, 2);

    public final SlotGroup chargeSlots = createSlotGroup(4, 0);

    public MGuiTestBlockContainerMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, (MGuiTestBlockEntity) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()));
    }

    public MGuiTestBlockContainerMenu(int containerId, Inventory inventory, MGuiTestBlockEntity blockEntity) {
        super(TestContainers.MGUI_TEST_BLOCK_CONTAINER.get(), containerId, inventory, blockEntity);

        progressSync = new DataSync<>(this, new ByteData(), () -> (byte) blockEntity.progress);
        energy = new DataSync<>(this, new LongData(), () -> blockEntity.energy.getEnergyStored());
        waterTank = new DataSync<>(this, new FluidData(), () -> blockEntity.waterStorage);
        lavaTank = new DataSync<>(this, new FluidData(), () -> blockEntity.lavaStorage);

        //When shift-clicking to player inventory, groups are processed in the order they are added.
        //So in this case we will try to move to main slots first, then hotBar.
        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);

        armor.addPlayerArmor(inventory);
        offhand.addPlayerOffhand(inventory);

        //Setup machine slots
        machineInputs.addSlots(3, 0, index -> new PolySlot(blockEntity.inventory, index)
                .setValidator(stack ->
                        switch (index) {
                            case 0 -> stack.is(Items.REDSTONE);
                            case 1 -> stack.is(Items.LAVA_BUCKET);
                            default -> stack.is(Items.WATER_BUCKET);
                        }
                ));
        machineOutputs.addAllSlots(blockEntity.outputInv, (container, integer) -> new PolySlot(container, integer).output());

        chargeSlots.addAllSlots(blockEntity.energyItemInv, PolySlot::new);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
