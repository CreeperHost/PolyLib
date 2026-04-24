package net.creeperhost.polylib.inventory.power;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface PolyEnergyBlock {
    IPolyEnergyStorage getEnergyStorage(@Nullable Direction side);
}
