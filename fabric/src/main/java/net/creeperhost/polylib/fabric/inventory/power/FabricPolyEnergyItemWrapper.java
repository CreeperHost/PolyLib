package net.creeperhost.polylib.fabric.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class FabricPolyEnergyItemWrapper extends FabricPolyEnergyWrapper implements IPolyEnergyStorageItem {

    private final ContainerItemContext context;

    public FabricPolyEnergyItemWrapper(EnergyStorage storage, ContainerItemContext context) {
        super(storage);
        this.context = context;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        if (context.getItemVariant().isBlank()) return ItemStack.EMPTY;
        return context.getItemVariant().toStack((int) context.getAmount());
    }
}
