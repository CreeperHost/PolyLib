package net.creeperhost.polylib.inventory.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SimpleItemInventory implements SerializableContainer
{
    private final NonNullList<ItemStack> items;
    private final BlockEntity blockEntity;
    private final Predicate<Player> canUse;

    public SimpleItemInventory(BlockEntity blockEntity, int size, Predicate<Player> canUse)
    {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.blockEntity = blockEntity;
        this.canUse = canUse;
    }

    public SimpleItemInventory(BlockEntity blockEntity, int size)
    {
        this(blockEntity, size, player -> blockEntity.getBlockPos().distSqr(player.blockPosition()) <= 64);
    }

    @Override
    public int getContainerSize()
    {
        return items.size();
    }

    @Override
    public boolean isEmpty()
    {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j)
    {
        return ContainerHelper.removeItem(items, i, j);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i)
    {
        return ContainerHelper.takeItem(items, i);
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack)
    {
        items.set(i, itemStack);
    }

    @Override
    public void setChanged()
    {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return canUse.test(player);
    }

    @Override
    public void clearContent()
    {
        items.clear();
    }

    @Override
    public void deserialize(CompoundTag compoundTag)
    {
        ContainerHelper.loadAllItems(compoundTag, items);
    }

    @Override
    public CompoundTag serialize(CompoundTag compoundTag)
    {
        return ContainerHelper.saveAllItems(compoundTag, items);
    }
}
