package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 16/02/2024
 */
public class FluidCapProvider implements ICapabilityProvider {
    private final PolyFluidBlock fluidBlock;

    public FluidCapProvider(PolyFluidBlock fluidBlock) {
        this.fluidBlock = fluidBlock;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == Capabilities.FLUID_HANDLER) {
            if (fluidBlock.getFluidHandler(arg) == null) return LazyOptional.empty();
            return LazyOptional.of(() -> new PolyNeoFluidWrapper(fluidBlock.getFluidHandler(arg))).cast();
        }
        return LazyOptional.empty();
    }
}
