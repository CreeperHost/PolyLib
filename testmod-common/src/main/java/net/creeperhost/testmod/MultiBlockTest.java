package net.creeperhost.testmod;

import net.creeperhost.polylib.mulitblock.IMultiblockPart;
import net.creeperhost.polylib.mulitblock.MultiblockControllerBase;
import net.creeperhost.polylib.mulitblock.rectangular.RectangularMultiblockControllerBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class MultiBlockTest extends RectangularMultiblockControllerBase
{
    public MultiBlockTest(Level world)
    {
        super(world);
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, CompoundTag data)
    {

    }

    @Override
    protected void onBlockAdded(IMultiblockPart newPart)
    {

    }

    @Override
    protected void onBlockRemoved(IMultiblockPart oldPart)
    {

    }

    @Override
    protected void onMachineAssembled()
    {
        TestMod.LOGGER.info("Multiblock created");
        //We should send a packet to the client when this happens
    }

    @Override
    protected void onMachineRestored()
    {
        TestMod.LOGGER.info("Multiblock restored");
    }

    @Override
    protected void onMachinePaused()
    {

    }

    @Override
    protected void onMachineDisassembled()
    {
        //We should send a packet to the client when this happens
    }

    @Override
    protected int getMinimumNumberOfBlocksForAssembledMachine()
    {
        return (9 * 3);
    }

    @Override
    protected int getMaximumXSize()
    {
        return 6;
    }

    @Override
    protected int getMaximumZSize()
    {
        return 6;
    }

    @Override
    protected int getMaximumYSize()
    {
        return 6;
    }

    @Override
    protected void onAssimilate(MultiblockControllerBase assimilated)
    {

    }

    @Override
    protected void onAssimilated(MultiblockControllerBase assimilator)
    {

    }

    @Override
    protected boolean updateServer()
    {
        return true;
    }

    @Override
    protected void updateClient()
    {

    }

    @Override
    public void writeToNBT(CompoundTag data)
    {

    }

    @Override
    public void readFromNBT(CompoundTag data)
    {

    }

    @Override
    public void formatDescriptionPacket(CompoundTag data)
    {

    }

    @Override
    public void decodeDescriptionPacket(CompoundTag data)
    {

    }
}
