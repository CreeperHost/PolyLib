package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorageItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Item variant of {@link FabricPolyFluidWrapper}.
 * <p>
 * Exposes Fabric item fluid storage through PolyLib's item storage contract and
 * returns the current container stack from the Fabric item context.
 */
public class FabricPolyFluidItemWrapper extends FabricPolyFluidWrapper implements IPolyFluidStorageItem {
    private final ContainerItemContext context;

    /**
     * @param storage native Fabric item fluid storage to wrap
     * @param context item context backing the native storage
     */
    public FabricPolyFluidItemWrapper(Storage<FluidVariant> storage, ContainerItemContext context) {
        super(storage);
        this.context = context;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        if (context.getItemVariant().isBlank()) return ItemStack.EMPTY;
        return context.getItemVariant().toStack((int) context.getAmount());
    }
}
