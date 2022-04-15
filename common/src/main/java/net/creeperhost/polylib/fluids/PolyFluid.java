package net.creeperhost.polylib.fluids;

import net.creeperhost.polylib.PolyLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class PolyFluid extends FlowingFluid
{
    @Override
    public Fluid getFlowing()
    {
        return PolyLib.TEST_FLUID_FLOWING.get();
    }

    @Override
    public Fluid getSource()
    {
        return PolyLib.TEST_FLUID.get();
    }

    @Override
    public Item getBucket()
    {
        return null;
    }

    @Override
    public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
        if (!fluidState.isSource() && !(Boolean)fluidState.getValue(FALLING)) {
            if (random.nextInt(64) == 0) {
                level.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.UNDERWATER, (double)blockPos.getX() + random.nextDouble(), (double)blockPos.getY() + random.nextDouble(), (double)blockPos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    @Nullable
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockState.hasBlockEntity() ? levelAccessor.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, levelAccessor, blockPos, blockEntity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader levelReader) {
        return 4;
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return (BlockState) PolyLib.FLUID_BLOCK.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
    }

    @Override
    public boolean isSource(FluidState fluidState)
    {
        return true;
    }

    public boolean isSame(Fluid fluid) {
        return fluid == PolyLib.TEST_FLUID.get() || fluid == PolyLib.TEST_FLUID_FLOWING.get();
    }

    @Override
    public int getDropOff(LevelReader levelReader) {
        return 1;
    }

    @Override
    public int getAmount(FluidState fluidState)
    {
        return 8;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 5;
    }
    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends PolyFluid
    {
        public Flowing() {}

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(new Property[]{LEVEL});
        }

        public int getAmount(FluidState fluidState) {
            return (Integer)fluidState.getValue(LEVEL);
        }

        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }
}
