package net.creeperhost.polylib.forge.inventory.fluid;

import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            if (fluidBlock.getFluidHandler(arg) == null) return LazyOptional.empty();
            return LazyOptional.of(() -> new PolyForgeFluidWrapper(fluidBlock.getFluidHandler(arg))).cast();
        }
        return LazyOptional.empty();
    }
}
