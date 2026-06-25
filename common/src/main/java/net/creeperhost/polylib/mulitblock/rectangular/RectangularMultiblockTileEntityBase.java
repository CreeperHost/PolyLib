package net.creeperhost.polylib.mulitblock.rectangular;

import net.creeperhost.polylib.mulitblock.MultiblockBlockEntityBase;
import net.creeperhost.polylib.mulitblock.MultiblockControllerBase;
import net.creeperhost.polylib.mulitblock.MultiblockValidationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base part implementation for rectangular multiblocks.
 * <p>
 * Tracks this part's position within the controller bounding box and the outward
 * facing direction for face parts. Concrete implementations provide validation
 * rules for frame, side, top, bottom, and interior positions.
 */
public abstract class RectangularMultiblockTileEntityBase extends MultiblockBlockEntityBase
{
    PartPosition position;
    Direction outwards;

    /**
     * Creates a rectangular multiblock part.
     *
     * @param tileEntityTypeIn the block entity type
     * @param blockPos         this part's world position
     * @param blockState       this part's block state
     */
    public RectangularMultiblockTileEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState)
    {
        super(tileEntityTypeIn, blockPos, blockState);

        position = PartPosition.Unknown;
        outwards = null;
    }

    // Positional Data
    /**
     * @return the outward direction for face parts, or null for interior/frame parts
     */
    public Direction getOutwardsDir()
    {
        return outwards;
    }

    /**
     * @return this part's position classification inside the rectangular machine
     */
    public PartPosition getPartPosition()
    {
        return position;
    }

    // Handlers from MultiblockTileEntityBase
    @Override
    public void onAttached(MultiblockControllerBase newController)
    {
        super.onAttached(newController);
        recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controller)
    {
        BlockPos maxCoord = controller.getMaximumCoord();
        BlockPos minCoord = controller.getMinimumCoord();

        // Discover where I am on the reactor
        recalculateOutwardsDirection(minCoord, maxCoord);
    }

    @Override
    public void onMachineBroken()
    {
        position = PartPosition.Unknown;
        outwards = null;
    }

    // Positional helpers
    /**
     * Recomputes this part's position classification and outward direction.
     *
     * @param minCoord the controller minimum bounding-box coordinate
     * @param maxCoord the controller maximum bounding-box coordinate
     */
    public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord)
    {
        outwards = null;
        position = PartPosition.Unknown;

        int facesMatching = 0;
        if (maxCoord.getX() == this.getBlockPos().getX() || minCoord.getX() == this.getBlockPos().getX())
        {
            facesMatching++;
        }
        if (maxCoord.getY() == this.getBlockPos().getY() || minCoord.getY() == this.getBlockPos().getY())
        {
            facesMatching++;
        }
        if (maxCoord.getZ() == this.getBlockPos().getZ() || minCoord.getZ() == this.getBlockPos().getZ())
        {
            facesMatching++;
        }

        if (facesMatching <= 0)
        {
            position = PartPosition.Interior;
        } else if (facesMatching >= 3)
        {
            position = PartPosition.FrameCorner;
        } else if (facesMatching == 2)
        {
            position = PartPosition.Frame;
        } else
        {
            // 1 face matches
            if (maxCoord.getX() == this.getBlockPos().getX())
            {
                position = PartPosition.EastFace;
                outwards = Direction.EAST;
            } else if (minCoord.getX() == this.getBlockPos().getX())
            {
                position = PartPosition.WestFace;
                outwards = Direction.WEST;
            } else if (maxCoord.getZ() == this.getBlockPos().getZ())
            {
                position = PartPosition.SouthFace;
                outwards = Direction.SOUTH;
            } else if (minCoord.getZ() == this.getBlockPos().getZ())
            {
                position = PartPosition.NorthFace;
                outwards = Direction.NORTH;
            } else if (maxCoord.getY() == this.getBlockPos().getY())
            {
                position = PartPosition.TopFace;
                outwards = Direction.UP;
            } else
            {
                position = PartPosition.BottomFace;
                outwards = Direction.DOWN;
            }
        }
    }

    // /// Validation Helpers (IMultiblockPart)
    /**
     * Validates this part for a frame or corner position.
     */
    public abstract void isGoodForFrame() throws MultiblockValidationException;

    /**
     * Validates this part for a side-wall position.
     */
    public abstract void isGoodForSides() throws MultiblockValidationException;

    /**
     * Validates this part for a top-face position.
     */
    public abstract void isGoodForTop() throws MultiblockValidationException;

    /**
     * Validates this part for a bottom-face position.
     */
    public abstract void isGoodForBottom() throws MultiblockValidationException;

    /**
     * Validates this part for an interior position.
     */
    public abstract void isGoodForInterior() throws MultiblockValidationException;
}
