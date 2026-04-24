package net.creeperhost.polylib.neoforge.inventory.power;

import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created by brandon3055 on 06/10/2025
 */
public class NeoPolyEnergyItemWrapper extends NeoPolyEnergyWrapper implements IPolyEnergyStorageItem {

    private final ItemAccess access;

    public NeoPolyEnergyItemWrapper(EnergyHandler handler, ItemAccess access) {
        super(handler);
        this.access = access;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return access.getResource().toStack(access.getAmount());
    }
}
