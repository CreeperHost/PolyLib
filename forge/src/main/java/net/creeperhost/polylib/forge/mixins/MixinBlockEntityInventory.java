package net.creeperhost.polylib.forge.mixins;

import net.creeperhost.polylib.blockentity.BlockEntityInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityInventory.class)
public abstract class MixinBlockEntityInventory extends BaseContainerBlockEntity implements WorldlyContainer
{
    public MixinBlockEntityInventory(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3)
    {
        super(arg, arg2, arg3);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if(cap == ForgeCapabilities.ITEM_HANDLER)
        {
            var caps = SidedInvWrapper.create(this, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
            if(side == null) return caps[0].cast();
            return caps[side.get3DDataValue()].cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if(cap == ForgeCapabilities.ITEM_HANDLER)
        {
            var caps = SidedInvWrapper.create(this, Direction.UP);
            return caps[0].cast();
        }
        return LazyOptional.empty();
    }
}
