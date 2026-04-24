package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.registry.IMenuFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

public interface IRegisterHelper {
    <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory);

    void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> dataWriter);
}
