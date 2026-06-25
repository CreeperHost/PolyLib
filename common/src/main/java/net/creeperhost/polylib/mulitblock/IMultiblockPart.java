package net.creeperhost.polylib.mulitblock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Base block entity contract for a block that can participate in a multiblock structure.
 * <p>
 * Parts are responsible for finding neighboring parts, attaching to compatible
 * controllers, tracking validation visits, and receiving machine lifecycle callbacks.
 */
public abstract class IMultiblockPart extends BlockEntity implements MenuProvider
{
    /**
     * Distance marker used when no valid graph distance is available.
     */
    public static final int INVALID_DISTANCE = Integer.MAX_VALUE;

    /**
     * Creates a multiblock part block entity.
     *
     * @param tileEntityTypeIn the block entity type
     * @param blockPos         this part's world position
     * @param blockState       this part's block state
     */
    public IMultiblockPart(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState)
    {
        super(tileEntityTypeIn, blockPos, blockState);
    }

    /**
     * @return true when this part is attached to a controller
     */
    public abstract boolean isConnected();

    /**
     * @return the controller this part is currently attached to
     */
    public abstract MultiblockControllerBase getMultiblockController();

    /**
     * @return this part's position in the world
     */
    public abstract BlockPos getWorldLocation();

    /**
     * Called when this part attaches to a controller.
     *
     * @param newController the controller this part joined
     */
    public abstract void onAttached(MultiblockControllerBase newController);

    /**
     * Called when this part detaches from a controller.
     *
     * @param multiblockController the controller this part left
     */
    public abstract void onDetached(MultiblockControllerBase multiblockController);

    /**
     * Called when this part was separated from a controller during graph splitting.
     *
     * @param oldController     the previous controller
     * @param oldControllerSize the size of the previous controller before splitting
     * @param newControllerSize the size of this part's new controller group
     */
    public abstract void onOrphaned(MultiblockControllerBase oldController, int oldControllerSize, int newControllerSize);

    /**
     * Creates a new controller instance for this part's multiblock type.
     *
     * @return a new controller
     */
    public abstract MultiblockControllerBase createNewMultiblock();

    /**
     * @return the controller class this part is compatible with
     */
    public abstract Class<? extends MultiblockControllerBase> getMultiblockControllerType();

    /**
     * Called when this part's controller is assimilated into another controller.
     *
     * @param newController the controller that now owns this part
     */
    public abstract void onAssimilated(MultiblockControllerBase newController);


    /**
     * Set that this block has been visited by your validation algorithms.
     */
    public abstract void setVisited();

    /**
     * Set that this block has not been visited by your validation algorithms;
     */
    public abstract void setUnvisited();

    /**
     * @return True if this block has been visited by your validation algorithms
     * since the last reset.
     */
    public abstract boolean isVisited();

    /**
     * Called when this block becomes the designated block for saving data and
     * transmitting data across the wire.
     */
    public abstract void becomeMultiblockSaveDelegate();

    /**
     * Called when this block is no longer the designated block for saving data
     * and transmitting data across the wire.
     */
    public abstract void forfeitMultiblockSaveDelegate();

    /**
     * Is this block the designated save/load network delegate?
     *
     * @return Boolean
     */
    public abstract boolean isMultiblockSaveDelegate();

    /**
     * Returns an array containing references to neighboring IMultiblockPart
     * tile entities. Primarily a utility method. Only works after tileentity
     * construction, so it cannot be used in
     * MultiblockControllerBase::attachBlock.
     * <p>
     * This method is chunk-safe on the server; it will not query for parts in
     * chunks that are unloaded. Note that no method is chunk-safe on the
     * client, because ChunkProviderClient is stupid.
     *
     * @return An array of references to neighboring IMultiblockPart tile
     * entities.
     */
    public abstract IMultiblockPart[] getNeighboringParts();

    /**
     * Called when a machine is fully assembled from the disassembled state,
     * meaning it was broken by a player/entity action, not by chunk unloads.
     * Note that, for non-square machines, the min/max coordinates may not
     * actually be part of the machine! They form an outer bounding box for the
     * whole machine itself.
     *
     * @param multiblockControllerBase The controller to which this part is being assembled.
     */
    public abstract void onMachineAssembled(MultiblockControllerBase multiblockControllerBase);

    /**
     * Called when the machine is broken for game reasons, e.g. a player removed
     * a block or an explosion occurred.
     */
    public abstract void onMachineBroken();

    /**
     * Called when the user activates the machine. This is not called by
     * default, but is included as most machines have this game-logical concept.
     */
    public abstract void onMachineActivated();

    /**
     * Called when the user deactivates the machine. This is not called by
     * default, but is included as most machines have this game-logical concept.
     */
    public abstract void onMachineDeactivated();

    // Block events

    /**
     * Called when this part should check its neighbors. This method MUST NOT
     * cause additional chunks to load. ALWAYS check to see if a chunk is loaded
     * before querying for its tile entity This part should inform the
     * controller that it is attaching at this time.
     *
     * @return A Set of multiblock controllers to which this object would like
     * to attach. It should have attached to one of the controllers in
     * this list. Return null if there are no compatible controllers
     * nearby.
     */
    public abstract Set<MultiblockControllerBase> attachToNeighbors();

    /**
     * Assert that this part is detached. If not, log a warning and set the
     * part's controller to null. Do NOT fire the full disconnection logic.
     */
    public abstract void assertDetached();

    /**
     * @return True if a part has multiblock game-data saved inside it.
     */
    public abstract boolean hasMultiblockSaveData();

    /**
     * @return The part's saved multiblock game-data in NBT format, or null if
     * there isn't any.
     */
    public abstract CompoundTag getMultiblockSaveData();

    /**
     * Called after a block is added and the controller has incorporated the
     * part's saved multiblock game-data into itself. Generally, you should
     * clear the saved data here.
     */
    public abstract void onMultiblockDataAssimilated();

    /**
     * @return true if this block entity has been invalidated and should be ignored
     */
    public boolean isInvalid()
    {
        return false;
    }
}
