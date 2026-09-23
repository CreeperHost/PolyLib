package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Block entity backed fluid storage.
 * <p>
 * Calls {@link BlockEntity#setChanged()} whenever the stored fluid changes.
 */
public class PolyBlockFluidStorage extends PolyFluidStorage {
    private final BlockEntity blockEntity;

    /**
     * @param blockEntity block entity to mark dirty
     * @param capacity tank capacity in droplets
     */
    public PolyBlockFluidStorage(BlockEntity blockEntity, long capacity) {
        super(capacity);
        this.blockEntity = blockEntity;
    }

    /**
     * @param blockEntity block entity to mark dirty
     * @param capacity tank capacity in droplets
     * @param changeListener optional listener invoked before the block entity is marked dirty
     */
    public PolyBlockFluidStorage(BlockEntity blockEntity, long capacity, Runnable changeListener) {
        super(capacity, changeListener);
        this.blockEntity = blockEntity;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        blockEntity.setChanged();
    }
}
