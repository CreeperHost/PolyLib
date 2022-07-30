package net.creeperhost.polylib.blockentity;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.containers.slots.SlotInput;
import net.creeperhost.polylib.containers.slots.SlotOutput;
import net.creeperhost.polylib.inventory.PolyItemInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class BlockEntityInventory extends BaseContainerBlockEntity implements WorldlyContainer
{
    private Optional<PolyItemInventory> inventoryOptional = Optional.empty();
    private final List<Slot> slots = new ArrayList<>();
    private SimpleContainerData containerData = new SimpleContainerData(0);

    public BlockEntityInventory(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState)
    {
        super(blockEntityType, blockPos, blockState);
    }

    public void setContainerDataSize(int value)
    {
        this.containerData = new SimpleContainerData(value);
    }

    public SimpleContainerData getContainerData()
    {
        return containerData;
    }

    public void setContainerDataValue(int i, int value)
    {
        if(containerData == null)
        {
            PolyLib.LOGGER.error("failed to set container data due to containerData being null");
            return;
        }
        if(i > containerData.getCount())
        {
            PolyLib.LOGGER.error("failed to set container data due to containerData size being lower then " + i);
            return;
        }

        containerData.set(i, value);
    }

    public void setContainerDataValue(int i, Supplier<Integer> value)
    {
        if(containerData == null)
        {
            PolyLib.LOGGER.error("failed to set container data due to containerData being null");
            return;
        }
        if(containerData.getCount() == 0 || i > containerData.getCount())
        {
            PolyLib.LOGGER.error("failed to set container data due to containerData size being lower then " + i);
            setContainerDataSize(i + 1);
        }

        try
        {
            containerData.set(i, value.get());
        } catch (Exception ignored){}
    }

    public void setInventory(@Nullable PolyItemInventory polyInventory)
    {
        inventoryOptional = Optional.ofNullable(polyInventory);
    }

    public Optional<PolyItemInventory> getInventoryOptional()
    {
        return inventoryOptional;
    }

    public void addSlot(Slot slot)
    {
        slots.add(slot);
    }

    public List<Slot> getSlots()
    {
        return slots;
    }

    @Override
    public int getContainerSize()
    {
        return getInventoryOptional().isPresent() ? getInventoryOptional().get().getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty()
    {
        return getInventoryOptional().isPresent() && getInventoryOptional().get().isEmpty();
    }

    @Override
    public ItemStack getItem(int i)
    {
        return getInventoryOptional().isPresent() ? getInventoryOptional().get().getItem(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j)
    {
        return getInventoryOptional().isPresent() ? getInventoryOptional().get().removeItem(i, j) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i)
    {
        return getInventoryOptional().isPresent() ? getInventoryOptional().get().removeItemNoUpdate(i) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack)
    {
        getInventoryOptional().ifPresent(polyInventory -> polyInventory.setItem(i, itemStack));
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return getInventoryOptional().isPresent() && getInventoryOptional().get().stillValid(player);
    }

    @Override
    public void clearContent()
    {
        getInventoryOptional().ifPresent(PolyItemInventory::clearContent);
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction)
    {
        return new int[getContainerSize()];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, @NotNull ItemStack itemStack, @org.jetbrains.annotations.Nullable Direction direction)
    {
        if(getSlots().size() >= i && getSlots().get(i) instanceof SlotOutput) return false;
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, @NotNull ItemStack itemStack, @NotNull Direction direction)
    {
        if(getSlots().size() >= i && getSlots().get(i) instanceof SlotInput) return false;
        return false;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag)
    {
        super.saveAdditional(compoundTag);
        getInventoryOptional().ifPresent(polyInventory -> compoundTag.merge(polyInventory.serializeNBT()));
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag)
    {
        super.load(compoundTag);
        getInventoryOptional().ifPresent(polyInventory -> polyInventory.deserializeNBT(compoundTag));
    }

}
