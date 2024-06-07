package net.creeperhost.polylib.fabric.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 18/02/2024
 */
public class FabricPolyFluidIemWrapper extends FabricPolyFluidWrapper implements PolyFluidHandlerItem {

    private final ContainerItemContext context;

    public FabricPolyFluidIemWrapper(ContainerItemContext context, Storage<FluidVariant> storage) {
        super(storage);
        this.context = context;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        if (context.getItemVariant().isBlank()) return ItemStack.EMPTY;
        return context.getItemVariant().toStack((int) context.getAmount());
    }
}
