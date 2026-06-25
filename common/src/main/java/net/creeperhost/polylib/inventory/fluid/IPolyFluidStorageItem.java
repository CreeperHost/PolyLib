package net.creeperhost.polylib.inventory.fluid;

import net.creeperhost.polylib.init.DataComps;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fluid storage backed by an {@link ItemStack}.
 * <p>
 * Platform item fluid APIs may replace the container stack after mutation, so
 * callers should read {@link #getContainer()} after fill or drain operations.
 */
public interface IPolyFluidStorageItem extends IPolyFluidStorage {
    /**
     * @return the current item container after any fluid operation
     */
    @NotNull
    ItemStack getContainer();

    /**
     * @return data component used by the default item storage implementation
     */
    default DataComponentType<PolyFluidStack> getFluidComponent() {
        return DataComps.getItemFluid();
    }
}
