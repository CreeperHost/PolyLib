package net.creeperhost.polylib.mulitblock;

import net.creeperhost.polylib.PolyLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MultiblockBlockEntityBase extends IMultiblockPart
{
    private MultiblockControllerBase controller;
    private boolean visited;

    private boolean saveMultiblockData;
    private CompoundTag cachedMultiblockData;

    public MultiblockBlockEntityBase(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState)
    {
        super(tileEntityTypeIn, blockPos, blockState);
        controller = null;
        visited = false;
        saveMultiblockData = false;
        cachedMultiblockData = null;
    }

    // /// Multiblock Connection Base Logic
    @Override
    public Set<MultiblockControllerBase> attachToNeighbors()
    {
        Set<MultiblockControllerBase> controllers = null;
        MultiblockControllerBase bestController = null;

        // Look for a compatible controller in our neighboring parts.
        IMultiblockPart[] partsToCheck = getNeighboringParts();
        for (IMultiblockPart neighborPart : partsToCheck)
        {
            if (neighborPart.isConnected())
            {
                MultiblockControllerBase candidate = neighborPart.getMultiblockController();
                if (!candidate.getClass().equals(this.getMultiblockControllerType()))
                {
                    // Skip multiblocks with incompatible types
                    continue;
                }

                if (controllers == null)
                {
                    controllers = new HashSet<MultiblockControllerBase>();
                    bestController = candidate;
                } else if (!controllers.contains(candidate) && candidate.shouldConsume(bestController))
                {
                    bestController = candidate;
                }

                controllers.add(candidate);
            }
        }

        // If we've located a valid neighboring controller, attach to it.
        if (bestController != null)
        {
            // attachBlock will call onAttached, which will set the controller.
            this.controller = bestController;
            bestController.attachBlock(this);
        }

        return controllers;
    }

    @Override
    public void assertDetached()
    {
        if (this.controller != null)
        {
            PolyLib.LOGGER.info(String.format(
                    "[assert] Part @ (%d, %d, %d) should be detached already, but detected that it was not. This is not a fatal error, and will be repaired, but is unusual.",
                    getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()));
            this.controller = null;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider)
    {
        super.loadAdditional(compoundTag, provider);
        // We can't directly initialize a multiblock controller yet, so we cache
        // the data here until
        // we receive a validate() call, which creates the controller and hands
        // off the cached data.
        if (!compoundTag.getCompound("multiblockData").isEmpty())
        {
            this.cachedMultiblockData = compoundTag.getCompound("multiblockData");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider)
    {
        super.saveAdditional(compoundTag, provider);
        if (isMultiblockSaveDelegate() && isConnected())
        {
            CompoundTag multiblockData = new CompoundTag();
            this.controller.writeToNBT(multiblockData);
            compoundTag.put("multiblockData", multiblockData);
        }
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        detachSelf(false);
    }

    @Override
    public void setLevel(Level level)
    {
        super.setLevel(level);
        MultiblockRegistry.onPartAdded(this.getLevel(), this);
    }

    /**
     * Override this to easily modify the description packet's data without
     * having to worry about sending the packet itself. Decode this data in
     * decodeDescriptionPacket.
     *
     * @param packetData An NBT compound tag into which you should write your custom
     *                   description data.
     */
    protected void encodeDescriptionPacket(CompoundTag packetData)
    {
        if (this.isMultiblockSaveDelegate() && isConnected())
        {
            CompoundTag tag = new CompoundTag();
            getMultiblockController().formatDescriptionPacket(tag);
            packetData.put("multiblockData", tag);
        }
    }

    /**
     * Override this to easily read in data from a TileEntity's description
     * packet. Encoded in encodeDescriptionPacket.
     *
     * @param packetData The NBT data from the tile entity's description packet.
     */
    protected void decodeDescriptionPacket(CompoundTag packetData)
    {
        if (packetData.get("multiblockData") != null)
        {
            CompoundTag tag = packetData.getCompound("multiblockData");
            if (isConnected())
            {
                getMultiblockController().decodeDescriptionPacket(tag);
            } else
            {
                // This part hasn't been added to a machine yet, so cache the data.
                this.cachedMultiblockData = tag;
            }
        }
    }

    @Override
    public boolean hasMultiblockSaveData()
    {
        return this.cachedMultiblockData != null;
    }

    @Override
    public CompoundTag getMultiblockSaveData()
    {
        return this.cachedMultiblockData;
    }

    @Override
    public void onMultiblockDataAssimilated()
    {
        this.cachedMultiblockData = null;
    }

    @Override
    public abstract void onMachineAssembled(MultiblockControllerBase multiblockControllerBase);

    @Override
    public abstract void onMachineBroken();

    @Override
    public abstract void onMachineActivated();

    @Override
    public abstract void onMachineDeactivated();

    @Override
    public boolean isConnected()
    {
        return (controller != null);
    }

    @Override
    public MultiblockControllerBase getMultiblockController()
    {
        return controller;
    }

    @Override
    public BlockPos getWorldLocation()
    {
        return this.getBlockPos();
    }

    @Override
    public void becomeMultiblockSaveDelegate()
    {
        this.saveMultiblockData = true;
    }

    @Override
    public void forfeitMultiblockSaveDelegate()
    {
        this.saveMultiblockData = false;
    }

    @Override
    public boolean isMultiblockSaveDelegate()
    {
        return this.saveMultiblockData;
    }

    @Override
    public void setUnvisited()
    {
        this.visited = false;
    }

    @Override
    public void setVisited()
    {
        this.visited = true;
    }

    @Override
    public boolean isVisited()
    {
        return this.visited;
    }

    @Override
    public void onAssimilated(MultiblockControllerBase newController)
    {
        assert (this.controller != newController);
        this.controller = newController;
    }

    @Override
    public void onAttached(MultiblockControllerBase newController)
    {
        this.controller = newController;
    }

    @Override
    public void onDetached(MultiblockControllerBase oldController)
    {
        this.controller = null;
    }

    @Override
    public abstract MultiblockControllerBase createNewMultiblock();

    @Override
    public IMultiblockPart[] getNeighboringParts()
    {
        BlockEntity te;
        List<IMultiblockPart> neighborParts = new ArrayList<IMultiblockPart>();
        BlockPos neighborPosition, partPosition = this.getWorldLocation();

        for (Direction facing : Direction.values())
        {
            neighborPosition = partPosition.relative(facing);
            te = this.level.getBlockEntity(neighborPosition);

            if (te instanceof IMultiblockPart) neighborParts.add((IMultiblockPart) te);
        }

        return neighborParts.toArray(new IMultiblockPart[neighborParts.size()]);
    }

    @Override
    public void onOrphaned(MultiblockControllerBase controller, int oldSize, int newSize)
    {

    }

    /*
     * Detaches this block from its controller. Calls detachBlock() and clears
     * the controller member.
     */
    protected void detachSelf(boolean chunkUnloading)
    {
        if (this.controller != null)
        {
            // Clean part out of controller
            this.controller.detachBlock(this, chunkUnloading);

            // The above should call onDetached, but, just in case...
            this.controller = null;
        }

        // Clean part out of lists in the registry
        MultiblockRegistry.onPartRemovedFromWorld(getLevel(), this);
    }
}
