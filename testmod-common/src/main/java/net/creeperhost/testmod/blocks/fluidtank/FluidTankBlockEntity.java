package net.creeperhost.testmod.blocks.fluidtank;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyBlockFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Test block entity that exposes a persistent 16 bucket PolyLib fluid storage.
 */
public class FluidTankBlockEntity extends PolyBlockEntity implements PolyFluidBlock, MenuProvider {
    /**
     * Tank capacity in droplets.
     */
    public static final long CAPACITY = FluidManager.BUCKET * 16;

    private final PolyBlockFluidStorage fluidTank = new PolyBlockFluidStorage(this, CAPACITY);

    /**
     * Creates a fluid tank block entity for the supplied position and state.
     *
     * @param pos block position
     * @param state current block state
     */
    public FluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(TestBlocks.FLUID_TANK_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * @return the tank storage backing this block entity
     */
    public PolyBlockFluidStorage getFluidTank() {
        return fluidTank;
    }

    /**
     * Provides the fluid storage exposed through platform fluid capabilities.
     *
     * @param side side being queried, or {@code null} for an unsided lookup
     * @return the tank's fluid storage
     */
    @Override
    public IPolyFluidStorage getFluidStorage(@Nullable Direction side) {
        return fluidTank;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Fluid Tank");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new FluidTankContainer(windowId, inventory, this);
    }

    /**
     * Saves the tank contents into the block entity's extra data payload.
     *
     * @param output output payload for this block entity
     */
    @Override
    public void writeExtraData(ValueOutput output) {
        fluidTank.serialize(output.child("fluid_tank"));
    }

    /**
     * Loads the tank contents from the block entity's extra data payload.
     *
     * @param input input payload for this block entity
     */
    @Override
    public void readExtraData(ValueInput input) {
        fluidTank.deserialize(input.childOrEmpty("fluid_tank"));
    }
}
