package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.DataManagerContainer;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.polylib.containers.network.ContainerSyncProtocol;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.creeperhost.polylib.data.serializable.ByteData;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerInventoryTest extends ModularGuiContainerMenu implements DataManagerContainer
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Tests server→client typed sync: server sends an energy snapshot string on open;
     * client logs receipt and bounces it back via CLIENT_ECHO.
     */
    public static final ContainerSyncProtocol.SyncPayloadType<String> SERVER_HELLO =
            new ContainerSyncProtocol.SyncPayloadType<>(
                    Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "container_sync_test/server_hello"),
                    ByteBufCodecs.STRING_UTF8.cast());

    /**
     * Tests client→server typed sync: client echoes the server hello string back.
     */
    public static final ContainerSyncProtocol.SyncPayloadType<String> CLIENT_ECHO =
            new ContainerSyncProtocol.SyncPayloadType<>(
                    Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "container_sync_test/client_echo"),
                    ByteBufCodecs.STRING_UTF8.cast());
    public final BlockEntityInventoryTest blockEntity;
    private boolean initialSyncSent = false;
    public final SlotGroup main = createSlotGroup(0, 1, 3); //zone id is 0, Quick move to zone 1, then 3
    public final SlotGroup hotBar = createSlotGroup(0, 1, 3);
    public final SlotGroup armor = createSlotGroup(1, 3, 0); //zone id is 1, Quick move to zone 3, then 0
    public final SlotGroup offhand = createSlotGroup(2, 3, 0);

    public final SlotGroup machineInputs = createSlotGroup(3, 1, 0, 2);//zone id is 3, Quick move to zone 1, then 0, then 2
    public final SlotGroup machineOutputs = createSlotGroup(3, 1, 0, 2);

    public final DataSync<Byte> progressSync;
    public final DataSync<Integer> energy;
    public final DataSync<Integer> maxEnergy;

    public ContainerInventoryTest(int id, Inventory playerInv, FriendlyByteBuf extraData)
    {
        this(id, playerInv, (BlockEntityInventoryTest) Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()));
    }

    public ContainerInventoryTest(int id, Inventory playerInv, BlockEntityInventoryTest inventoryTestBlock)
    {
        super(TestContainers.TEST_INVENTORY_CONTAINER.get(), id, playerInv);
        this.blockEntity = inventoryTestBlock;

        progressSync = new DataSync<>(this, new ByteData(), () -> (byte) blockEntity.progress);
        energy = new DataSync<>(this, new IntData(), () -> (int) blockEntity.energyContainer.getEnergyStored());
        maxEnergy = new DataSync<>(this, new IntData(), () -> (int) blockEntity.energyContainer.getMaxEnergyStored());

        // ContainerSyncProtocol test: client receives SERVER_HELLO and bounces it back as CLIENT_ECHO
        syncProtocol.registerClientHandler(SERVER_HELLO, (player, msg) -> {
            LOGGER.info("[ContainerSyncTest] Client received SERVER_HELLO: '{}'", msg);
            syncProtocol.sendToServer(containerId, CLIENT_ECHO, "echo: " + msg);
        });
        syncProtocol.registerServerHandler(CLIENT_ECHO, (player, msg) ->
                LOGGER.info("[ContainerSyncTest] Server received CLIENT_ECHO from {}: '{}'", player.getName().getString(), msg));

        main.addPlayerMain(inventory);
        hotBar.addPlayerBar(inventory);

        armor.addPlayerArmor(inventory);
        offhand.addPlayerOffhand(inventory);

        machineInputs.addSlots(1, 0, index -> new PolySlot(blockEntity.simpleItemInventory, index));
        machineOutputs.addAllSlots(blockEntity.getOutputContainer(), (container, integer) -> new PolySlot(container, integer).output());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!initialSyncSent && inventory.player instanceof ServerPlayer serverPlayer) {
            initialSyncSent = true;
            String hello = "energy=" + (int) blockEntity.energyContainer.getEnergyStored() + "/" + (int) blockEntity.energyContainer.getMaxEnergyStored();
            LOGGER.info("[ContainerSyncTest] Server sending SERVER_HELLO: '{}' containerId={}", hello, containerId);
            syncProtocol.sendToClient(serverPlayer, containerId, SERVER_HELLO, hello);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return true;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public <T extends BlockEntity & DataManagerBlock> T getBlockEntity() {
        return (T) blockEntity;
    }
}
