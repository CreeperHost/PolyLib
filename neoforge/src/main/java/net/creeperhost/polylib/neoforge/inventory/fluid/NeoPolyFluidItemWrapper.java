package net.creeperhost.polylib.neoforge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 18/02/2024
 */
public class NeoPolyFluidItemWrapper extends NeoPolyFluidWrapper implements PolyFluidHandlerItem {

    private final ItemAccess access;

    public NeoPolyFluidItemWrapper(ResourceHandler<FluidResource> handler, ItemAccess access) {
        super(handler);
        this.access = access;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return access.getResource().toStack(access.getAmount());
    }
}
