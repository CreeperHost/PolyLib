package net.creeperhost.polylib.registry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IMenuFactory<T extends AbstractContainerMenu> {
    T create(int syncId, Inventory inventory, @Nullable RegistryFriendlyByteBuf data);
}
