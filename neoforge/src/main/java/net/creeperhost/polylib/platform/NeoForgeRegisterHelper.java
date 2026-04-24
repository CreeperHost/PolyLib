package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.registry.IMenuFactory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.function.Consumer;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory) {
        return IMenuTypeExtension.create((id, inv, buf) -> factory.create(id, inv, buf));
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> dataWriter) {
        player.openMenu(menuProvider, dataWriter);
    }
}
