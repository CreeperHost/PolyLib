package net.creeperhost.testmod.blocks.machinetest;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineTestBlock extends BlockFacing
{
    public MachineTestBlock()
    {
        super(Properties.of());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState)
    {
        return null;
    }
}
