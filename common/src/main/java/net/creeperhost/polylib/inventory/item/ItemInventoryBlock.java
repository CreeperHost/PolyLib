package net.creeperhost.polylib.inventory.item;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ItemInventoryBlock
{
    @Deprecated //Use sided version
    default SerializableContainer getContainer() {
        return getContainer(null);
    }

    default SerializableContainer getContainer(@Nullable Direction side) {
        return getContainer();
    }
}
