package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativePowerBlockEntity extends BlockEntity implements PolyEnergyBlock, MenuProvider {
    private final IPolyEnergyStorage creativeStorage = new IPolyEnergyStorage() {
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
        return creativeStorage;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PowerContainer(i, inventory, null);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("test");
    }
}
