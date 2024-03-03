package net.creeperhost.polylib.forge.inventory.power;

import net.creeperhost.polylib.inventory.fluid.PolyFluidBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyBlock;
import net.creeperhost.polylib.inventory.power.PolyEnergyItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class EnergyCapProvider implements ICapabilityProvider {
    private PolyEnergyBlock energyBlock;
    private PolyEnergyItem item;
    private ItemStack stack;

    public EnergyCapProvider(PolyEnergyBlock energyBlock) {
        this.energyBlock = energyBlock;
    }

    public EnergyCapProvider(PolyEnergyItem item, ItemStack stack) {
        this.item = item;
        this.stack = stack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == Capabilities.ENERGY) {
            if (energyBlock != null) {
                if (energyBlock.getEnergyStorage(arg) == null) return LazyOptional.empty();
                return LazyOptional.of(() -> new PolyNeoEnergyWrapper(energyBlock.getEnergyStorage(arg))).cast();
            } else if (stack != null) {
                if (item.getEnergyStorage(stack) == null) return LazyOptional.empty();
                return LazyOptional.of(() -> new PolyNeoEnergyWrapper(item.getEnergyStorage(stack))).cast();
            }
        }
        return LazyOptional.empty();
    }
}
