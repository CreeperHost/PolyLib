package net.creeperhost.testmod.blocks.creativepower;

import net.creeperhost.polylib.inventory.energy.EnergyHooks;
import net.creeperhost.polylib.inventory.energy.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.energy.impl.CreativeEnergyContainer;
import net.creeperhost.polylib.inventory.energy.impl.WrappedBlockEnergyContainer;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativePowerBlockEntity extends BlockEntity implements PolyEnergyBlock<WrappedBlockEnergyContainer>
{
    private WrappedBlockEnergyContainer energyContainer;

    public CreativePowerBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(TestBlocks.CREATIVE_ENERGY_BLOCK_TILE.get(), blockPos, blockState);
    }

    @Override
    public WrappedBlockEnergyContainer getEnergyStorage()
    {
        return energyContainer == null ? this.energyContainer = new WrappedBlockEnergyContainer(this, new CreativeEnergyContainer(1000000)) : this.energyContainer;
    }

    public void tick()
    {
        EnergyHooks.distributeEnergyNearby(this);
    }
}
