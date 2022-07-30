package net.creeperhost.polylib.blockentity;

import net.creeperhost.polylib.inventory.PolyInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
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

public abstract class BlockEntityInventory extends BaseContainerBlockEntity
{
    private Optional<PolyInventory> inventoryOptional = Optional.empty();
    private final List<Slot> slots = new ArrayList<>();

    public BlockEntityInventory(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState)
    {
        super(blockEntityType, blockPos, blockState);
    }

    public void setInventory(@Nullable PolyInventory polyInventory)
    {
        inventoryOptional = Optional.ofNullable(polyInventory);
    }

    public Optional<PolyInventory> getInventoryOptional()
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
        getInventoryOptional().ifPresent(PolyInventory::clearContent);
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
