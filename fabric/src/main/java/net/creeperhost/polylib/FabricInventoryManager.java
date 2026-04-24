package net.creeperhost.polylib;

import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.ContainerStorageImpl;

public class FabricInventoryManager
{
    public static void init(){
        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity instanceof PolyInventoryBlock invBlock) {
                return ContainerStorageImpl.of(invBlock.getContainer(context), context);
            }
            return null;
        });
    }
}
