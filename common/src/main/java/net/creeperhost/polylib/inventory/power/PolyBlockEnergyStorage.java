package net.creeperhost.polylib.inventory.power;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class PolyBlockEnergyStorage extends PolyEnergyStorage {
    private final BlockEntity blockEntity;

    public PolyBlockEnergyStorage(BlockEntity blockEntity, long capacity) {
        super(capacity);
        this.blockEntity = blockEntity;
    }

    public PolyBlockEnergyStorage(BlockEntity blockEntity, long capacity, long maxTransfer) {
        super(capacity, maxTransfer);
        this.blockEntity = blockEntity;
    }

    public PolyBlockEnergyStorage(BlockEntity blockEntity, long capacity, long maxReceive, long maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.blockEntity = blockEntity;
    }

    public PolyBlockEnergyStorage(BlockEntity blockEntity, long capacity, long maxReceive, long maxExtract, Runnable changeListener) {
        super(capacity, maxReceive, maxExtract, changeListener);
        this.blockEntity = blockEntity;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        blockEntity.setChanged();
    }
}
