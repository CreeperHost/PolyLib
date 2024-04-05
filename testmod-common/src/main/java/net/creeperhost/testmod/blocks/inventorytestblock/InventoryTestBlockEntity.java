package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.data.TileDataManager;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.inventory.energy.impl.SimpleEnergyContainer;
import net.creeperhost.polylib.inventory.energy.impl.WrappedBlockEnergyContainer;
import net.creeperhost.polylib.inventory.item.ItemInventoryBlock;
import net.creeperhost.polylib.inventory.item.SerializableContainer;
import net.creeperhost.polylib.inventory.item.SimpleItemInventory;
import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyBlockEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyStorage;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryTestBlockEntity extends PolyBlockEntity implements PolyEnergyBlock, PolyInventoryBlock, MenuProvider, DataManagerBlock
{
    int progress = 0;

    public final BlockInventory simpleItemInventory = new BlockInventory(this, 1);
    public final BlockInventory outputInv = new BlockInventory(this, 1);
    public final PolyEnergyStorage energyContainer = new PolyBlockEnergyStorage(this, 1000000);

    public IntData testSyncedIntField = register("test_int", new IntData(0), SAVE, SYNC, CLIENT_CONTROL);

    public InventoryTestBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.INVENTORY_TEST_TILE.get(), blockPos, blockState);
    }

    public void tick()
    {
        super.tick();
        if(level != null && !level.isClientSide)
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
    public Container getContainer(@Nullable Direction side) {
        return simpleItemInventory;
    }


    public Container getOutputContainer() {
        return outputInv;
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyContainer;
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.literal("Inventory Test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player)
    {
        return new ContainerInventoryTestBlock(id, inventory, this);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void writeExtraData(CompoundTag nbt) {
        simpleItemInventory.serialize(nbt);
        nbt.put("out_inv", outputInv.serialize(new CompoundTag()));
    }

    @Override
    public void readExtraData(CompoundTag nbt) {
        simpleItemInventory.deserialize(nbt);
        outputInv.deserialize(nbt.getCompound("out_inv"));
    }

    @Override
    public void handlePacketFromClient(ServerPlayer player, int id, FriendlyByteBuf buf) {
        TestMod.LOGGER.info("Message from client! {}", player);
        Cow cow = new Cow(EntityType.COW, level);
        cow.setPos(Vec3.atBottomCenterOf(getBlockPos().above()));
        level.addFreshEntity(cow);
    }
}
