package net.creeperhost.polylib.inventory.items;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 26/02/2024
 */
public interface PolyInventoryBlock {

    Container getContainer(@Nullable Direction side);
}
