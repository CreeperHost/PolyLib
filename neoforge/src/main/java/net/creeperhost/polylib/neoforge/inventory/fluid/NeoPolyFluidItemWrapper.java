package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

/**
 * Item variant of {@link NeoPolyFluidWrapper}.
 * <p>
 * Keeps the {@link ItemAccess} used to query the item capability so the current
 * container stack can be returned after operations.
 */
public class NeoPolyFluidItemWrapper extends NeoPolyFluidWrapper implements IPolyFluidStorageItem {
    private final ItemAccess access;

    /**
     * @param handler native NeoForge item fluid handler to wrap
     * @param access item access backing the handler
     */
    public NeoPolyFluidItemWrapper(ResourceHandler<FluidResource> handler, ItemAccess access) {
        super(handler);
        this.access = access;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return access.getResource().toStack(access.getAmount());
    }
}
