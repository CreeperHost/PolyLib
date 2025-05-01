package net.creeperhost.polylib.fabric.inventory.power;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.fabric.TeamRebornEnergyCompat;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public class FabricEnergyManager implements EnergyManager {

    @Override
    public @Nullable IPolyEnergyStorage getBlockEnergyStorage(BlockEntity block, @Nullable Direction side) {
        if(Platform.isModLoaded("team_reborn_energy"))
        {
            return TeamRebornEnergyCompat.getBlockEnergyStorage(block, side);
        }
        return null;
    }

    @Override
    public @Nullable IPolyEnergyStorageItem getItemEnergyStorage(ItemStack stack) {
        if(Platform.isModLoaded("team_reborn_energy"))
        {
            return TeamRebornEnergyCompat.getItemEnergyStorage(stack);
        }
        return null;
    }
}
