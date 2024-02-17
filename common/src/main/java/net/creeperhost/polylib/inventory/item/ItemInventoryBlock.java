package net.creeperhost.polylib.inventory.item;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ItemInventoryBlock
{
    @Deprecated //Use sided version
    SerializableContainer getContainer();

    default SerializableContainer getContainer(@Nullable Direction side) {
        return getContainer();
    }
}
