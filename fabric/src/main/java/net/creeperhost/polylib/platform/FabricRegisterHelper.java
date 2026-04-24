package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.registry.IMenuFactory;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

public class FabricRegisterHelper implements IRegisterHelper {

    // Codec that round-trips a RegistryFriendlyByteBuf as a length-prefixed byte array.
    // Used by ExtendedMenuType so the server can write arbitrary data that the client factory reads back.
    private static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> RAW_BUF_CODEC = StreamCodec.of(
            (out, data) -> {
                byte[] bytes = new byte[data.readableBytes()];
                data.readBytes(bytes);
                out.writeByteArray(bytes);
            },
            in -> {
                byte[] bytes = in.readByteArray();
                return new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(bytes), in.registryAccess());
            }
    );

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory) {
        return new ExtendedMenuType<>((syncId, inv, buf) -> factory.create(syncId, inv, buf), RAW_BUF_CODEC);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> dataWriter) {
        player.openMenu(new ExtendedMenuProvider<RegistryFriendlyByteBuf>() {
            @Override
            public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer serverPlayer) {
                RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), serverPlayer.registryAccess());
                dataWriter.accept(buf);
                return buf;
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return menuProvider.createMenu(syncId, inv, player);
            }

            @Override
            public Component getDisplayName() {
                return menuProvider.getDisplayName();
            }
        });
    }
}