package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 18/02/2024
 */
public class NeoPolyFluidItemWrapper extends NeoPolyFluidWrapper implements PolyFluidHandlerItem {

    private final IFluidHandlerItem handler;

    public NeoPolyFluidItemWrapper(IFluidHandlerItem handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return handler.getContainer();
    }
}
