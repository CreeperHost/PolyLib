package net.creeperhost.polylib.inventory.fluid;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fluid handler item interface based on Forge's IFluidHandler
 * Created by brandon3055 on 18/02/2024
 */
public interface PolyFluidHandlerItem extends PolyFluidHandler {

    @NotNull
    ItemStack getContainer();
}
