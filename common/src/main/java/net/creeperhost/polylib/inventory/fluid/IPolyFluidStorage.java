package net.creeperhost.polylib.inventory.fluid;

/**
 * Common fluid storage contract used by PolyLib across loaders.
 * <p>
 * All amounts are expressed in droplets. Platform wrappers convert as needed
 * when bridging to native Fabric or NeoForge transfer APIs.
 */
public interface IPolyFluidStorage {
    /**
     * @return the current fluid contents
     */
    PolyFluidStack getFluid();

    /**
     * @return the maximum fluid capacity in droplets
     */
    long getCapacity();

    /**
     * Attempts to fill this storage.
     *
     * @param resource fluid and maximum amount to fill
     * @param simulate true to calculate without mutating
     * @return amount accepted in droplets
     */
    long fill(PolyFluidStack resource, boolean simulate);

    /**
     * Attempts to drain a matching fluid from this storage.
     *
     * @param resource fluid and maximum amount to drain
     * @param simulate true to calculate without mutating
     * @return drained fluid stack
     */
    PolyFluidStack drain(PolyFluidStack resource, boolean simulate);

    /**
     * Attempts to drain any available fluid from this storage.
     *
     * @param maxDrain maximum amount to drain in droplets
     * @param simulate true to calculate without mutating
     * @return drained fluid stack
     */
    PolyFluidStack drain(long maxDrain, boolean simulate);

    /**
     * @return true if this storage can accept the supplied fluid
     */
    boolean canFill(PolyFluidStack resource);

    /**
     * @return true if this storage can drain the supplied fluid
     */
    boolean canDrain(PolyFluidStack resource);

    /**
     * Checks whether the supplied fluid is valid for this storage, ignoring current contents.
     *
     * @param resource fluid to validate
     * @return true when this fluid type may be stored here
     */
    boolean isFluidValid(PolyFluidStack resource);

    /**
     * Directly replaces the stored fluid, clamping to capacity where appropriate.
     *
     * @param stack new contents
     */
    void setFluid(PolyFluidStack stack);
}
