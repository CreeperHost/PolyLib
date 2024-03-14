package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidHandlerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 18/02/2024
 */
public class ForgePolyFluidItemWrapper extends ForgePolyFluidWrapper implements PolyFluidHandlerItem {

    private final IFluidHandlerItem handler;

    public ForgePolyFluidItemWrapper(IFluidHandlerItem handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return handler.getContainer();
    }
}
