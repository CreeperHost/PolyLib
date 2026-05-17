package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.data.serializable.StringData;
import net.creeperhost.polylib.data.serializable.UUIDData;
import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyBlockEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyStorage;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class BlockEntityInventoryTest extends PolyBlockEntity implements PolyInventoryBlock, PolyEnergyBlock, MenuProvider
{
    /**
     * A user-editable label stored in NBT and synced to clients.
     * Tests StringData persistence + CLIENT_CONTROL round-trip.
     */
    public final StringData testLabel;

    /**
     * UUID of the last player who opened this container.
     * Set server-side in createMenu; tests UUIDData NBT persistence + sync.
     */
    public final UUIDData lastVisitorUUID;

    public final BlockInventory simpleItemInventory = new BlockInventory(this, 1);
    public final BlockInventory outputInv = new BlockInventory(this, 1);
    public final PolyEnergyStorage energyContainer = new PolyBlockEnergyStorage(this, 1000000);

    int progress = 0;
    public IntData testSyncedIntField = register("test_int", new IntData(0), SAVE, SYNC, CLIENT_CONTROL);


    public BlockEntityInventoryTest(BlockPos pos, BlockState state)
    {
        super(TestBlocks.TEST_BLOCK_ENTITY.get(), pos, state);
        testLabel = register("test_label", new StringData(""), SAVE_BOTH, SYNC, CLIENT_CONTROL);
        lastVisitorUUID = register("last_visitor_uuid", new UUIDData(), SAVE_BOTH, SYNC);
    }

    public void tick()
    {
        super.tick();
        if(level != null && !level.isClientSide())
        {
            progress++;
            if(progress >= 100)
            {
                progress = 0;
                getOutputContainer().setItem(0, new ItemStack(Items.DIAMOND));
                testSyncedIntField.set(testSyncedIntField.get() + 1);
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.literal("Inventory Test");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player)
    {
        lastVisitorUUID.set(player.getUUID());
        return new ContainerInventoryTest(i, inventory, this);
    }

    @Override
    public Container getContainer(@org.jetbrains.annotations.Nullable Direction side) {
        return this.simpleItemInventory;
    }

    public Container getOutputContainer() {
        return outputInv;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void writeExtraData(ValueOutput output) {
        simpleItemInventory.serialize(output);
        outputInv.serialize(output.child("out_inv"));
    }

    @Override
    public void readExtraData(ValueInput input) {
        simpleItemInventory.deserialize(input);
        outputInv.deserialize(input.childOrEmpty("out_inv"));
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(@org.jetbrains.annotations.Nullable Direction side) {
        return energyContainer;
    }

    @Override
    public void handlePacketFromClient(ServerPlayer player, int id, FriendlyByteBuf buf) {
        TestModCommon.LOGGER.info("Message from client! {}", player);
        Cow cow = new Cow(EntityType.COW, level);
        cow.setPos(Vec3.atBottomCenterOf(getBlockPos().above()));
        level.addFreshEntity(cow);
    }
}
