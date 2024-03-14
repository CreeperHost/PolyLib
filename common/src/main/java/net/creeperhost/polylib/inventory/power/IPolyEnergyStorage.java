package net.creeperhost.polylib.inventory.power;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface IPolyEnergyStorage {

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    long receiveEnergy(long maxReceive, boolean simulate);

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    long extractEnergy(long maxExtract, boolean simulate);

    /**
     * Returns the amount of energy currently stored.
     */
    long getEnergyStored();

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    long getMaxEnergyStored();

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractOP will return 0.
     */
    boolean canExtract();

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveOP will return 0.
     */
    boolean canReceive();

    /**
     * This method bypasses the usual can insert, can extract and transfer rate checks and modifies
     * the energy storage directly. This is primarily for internal use and is the method by which an
     * energy item / machine should modify its own energy value.
     * <p>
     * <strong>Only use this if you know what your doing!</strong>
     * <p>
     * Note: in cases where {@link IPolyEnergyStorage} is wrapping a forge or fabric energy storage this is still limited
     * by the receive/extract methods. So unless your sure your dealing with an actual {@link IPolyEnergyStorage}
     * do not just assume this will work.
     *
     * @param amount the amount (positive or negative)
     * @return The amount of energy that was actually added / removed (always positive or zero)
     */
    long modifyEnergyStored(long amount);
}
