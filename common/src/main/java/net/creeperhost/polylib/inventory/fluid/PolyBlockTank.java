package net.creeperhost.polylib.inventory.fluid;

import dev.architectury.fluid.FluidStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Simple implementation of PolyTank that ensures the given block entity is marked dirty when fluid content changes.
 * <p>
 * Created by brandon3055 on 26/02/2024
 */
public class PolyBlockTank extends PolyTank {
    private final BlockEntity blockEntity;

    public PolyBlockTank(BlockEntity blockEntity, long capacity) {
        super(capacity);
        this.blockEntity = blockEntity;
    }

    public PolyBlockTank(BlockEntity blockEntity, long capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.blockEntity = blockEntity;
    }

    public PolyBlockTank(BlockEntity blockEntity, long capacity, Predicate<FluidStack> validator, @Nullable Runnable dirtyListener) {
        super(capacity, validator, dirtyListener);
        this.blockEntity = blockEntity;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        blockEntity.setChanged();
    }
}
