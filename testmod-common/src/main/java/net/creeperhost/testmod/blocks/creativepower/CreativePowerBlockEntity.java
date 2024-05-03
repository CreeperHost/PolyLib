package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativePowerBlockEntity extends PolyBlockEntity implements PolyEnergyBlock {
    private IPolyEnergyStorage creativeStorage = new IPolyEnergyStorage() {
        @Override
        public long receiveEnergy(long maxReceive, boolean simulate) {
            return maxReceive;
        }

        @Override
        public long extractEnergy(long maxExtract, boolean simulate) {
            return maxExtract;
        }

        @Override
        public long getEnergyStored() {
            return Long.MAX_VALUE / 2;
        }

        @Override
        public long getMaxEnergyStored() {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public long modifyEnergyStored(long amount) {
            return amount;
        }
    };

    public CreativePowerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TestBlocks.CREATIVE_ENERGY_BLOCK_TILE.get(), blockPos, blockState);
    }


    public void tick() {
        EnergyManager.distributeEnergyNearby(this);
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return null;
    }
}
