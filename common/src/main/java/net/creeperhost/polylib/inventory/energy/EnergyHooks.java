package net.creeperhost.polylib.inventory.energy;

import com.mojang.datafixers.util.Pair;
import net.creeperhost.polylib.PolyLibPlatform;
import net.creeperhost.polylib.inventory.item.ItemStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyHooks
{
    public static PlatformItemEnergyManager getItemEnergyManager(ItemStack stack)
    {
        return PolyLibPlatform.getItemEnergyManager(stack);
    }

    public static PlatformEnergyManager getBlockEnergyManager(BlockEntity entity, @Nullable Direction direction)
    {
        return PolyLibPlatform.getBlockEnergyManager(entity, direction);
    }

    public static boolean isEnergyItem(ItemStack stack)
    {
        return PolyLibPlatform.isEnergyItem(stack);
    }

    public static boolean isEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction)
    {
        return PolyLibPlatform.isEnergyContainer(blockEntity, direction);
    }

    public static long moveEnergy(PlatformEnergyManager from, PlatformEnergyManager to, long amount)
    {
        long extracted = from.extract(amount, true);
        long inserted = to.insert(extracted, true);
        from.extract(inserted, false);
        return to.insert(inserted, false);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static long safeMoveEnergy(Optional<PlatformEnergyManager> from, Optional<PlatformEnergyManager> to, long amount)
    {
        return from.map(f -> to.map(t -> moveEnergy(f, t, amount)).orElse(0L)).orElse(0L);
    }

    public static Optional<PlatformEnergyManager> safeGetBlockEnergyManager(BlockEntity entity, @Nullable Direction direction)
    {
        return isEnergyContainer(entity, direction) ? Optional.of(getBlockEnergyManager(entity, direction)) : Optional.empty();
    }

    public static Optional<PlatformItemEnergyManager> safeGetItemEnergyManager(ItemStack stack)
    {
        return isEnergyItem(stack) ? Optional.of(getItemEnergyManager(stack)) : Optional.empty();
    }

    public static long moveItemToItemEnergy(PlatformItemEnergyManager from, ItemStackHolder sender, PlatformItemEnergyManager to, ItemStackHolder receiver, long amount)
    {
        long extracted = from.extract(sender.copy(), amount, true);
        long inserted = to.insert(receiver.copy(), extracted, true);
        from.extract(sender, inserted, false);
        return to.insert(receiver, inserted, false);
    }

    public static long moveStandardToItemEnergy(PlatformEnergyManager from, PlatformItemEnergyManager to, ItemStackHolder receiver, long amount)
    {
        long extracted = from.extract(amount, true);
        long inserted = to.insert(receiver.copy(), extracted, true);
        from.extract(inserted, false);
        return to.insert(receiver, inserted, false);
    }

    public static long moveItemToStandardEnergy(PlatformItemEnergyManager from, ItemStackHolder sender, PlatformEnergyManager to, long amount)
    {
        long extracted = from.extract(sender.copy(), amount, true);
        long inserted = to.insert(extracted, true);
        from.extract(sender, inserted, false);
        return to.insert(inserted, false);
    }

    public static long safeMoveItemToItemEnergy(ItemStackHolder from, ItemStackHolder to, long amount)
    {
        return safeGetItemEnergyManager(from.getStack()).map(f -> safeGetItemEnergyManager(to.getStack()).map(t -> moveItemToItemEnergy(f, from, t, to, amount)).orElse(0L)).orElse(0L);
    }

    public static long safeMoveItemToBlockEnergy(ItemStackHolder from, BlockEntity to, @Nullable Direction direction, long amount)
    {
        return safeGetItemEnergyManager(from.getStack()).map(f -> safeGetBlockEnergyManager(to, direction).map(t -> moveItemToStandardEnergy(f, from, t, amount)).orElse(0L)).orElse(0L);
    }

    public static long safeMoveBlockToItemEnergy(BlockEntity from, @Nullable Direction direction, ItemStackHolder to, long amount)
    {
        return safeGetBlockEnergyManager(from, direction).map(f -> safeGetItemEnergyManager(to.getStack()).map(t -> moveStandardToItemEnergy(f, t, to, amount)).orElse(0L)).orElse(0L);
    }

    public static long moveBlockToBlockEnergy(BlockEntity from, @Nullable Direction fromDirection, BlockEntity to, @Nullable Direction toDirection, long amount)
    {
        return safeMoveEnergy(safeGetBlockEnergyManager(from, fromDirection), safeGetBlockEnergyManager(to, toDirection), amount);
    }

    public static long moveBlockToBlockEnergy(BlockEntity from, BlockEntity to, long amount)
    {
        return safeMoveEnergy(safeGetBlockEnergyManager(from, null), safeGetBlockEnergyManager(to, null), amount);
    }

    public static <T extends BlockEntity & PolyEnergyBlock<?>> void distributeEnergyNearby(T energyBlock, long amount)
    {
        BlockPos blockPos = energyBlock.getBlockPos();
        Level level = energyBlock.getLevel();
        if (level == null) return;
        Direction.stream().map(
                direction -> Pair.of(direction, level.getBlockEntity(blockPos.relative(direction)))).filter(
                pair -> pair.getSecond() != null).map(
                pair -> Pair.of(safeGetBlockEnergyManager(pair.getSecond(), pair.getFirst()), pair.getFirst())).filter(
                pair -> pair.getFirst().isPresent()).forEach(pair ->
        {
            PlatformEnergyManager externalEnergy = pair.getFirst().get();
            safeGetBlockEnergyManager(energyBlock, pair.getSecond().getOpposite()).ifPresent(
                    platformEnergyManager -> moveEnergy(platformEnergyManager, externalEnergy,
                            amount == -1 ? energyBlock.getEnergyStorage().getStoredEnergy() : amount));
        });
    }

    public static <T extends BlockEntity & PolyEnergyBlock<?>> void distributeEnergyNearby(T energyBlock)
    {
        distributeEnergyNearby(energyBlock, -1);
    }

    public static int toDurabilityBar(PolyEnergyItem<?> energyItem, ItemStack stack)
    {
        PolyEnergyContainer energyStorage = energyItem.getEnergyStorage(stack);
        return (int) (((double) energyStorage.getStoredEnergy() / energyStorage.getMaxCapacity()) * 13);
    }
}
