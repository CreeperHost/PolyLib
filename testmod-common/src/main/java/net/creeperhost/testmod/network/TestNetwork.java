package net.creeperhost.testmod.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.containers.ModularGuiContainerMenu;
import net.creeperhost.testmod.TestMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

/**
 * Created by brandon3055 on 09/09/2023
 */
public class TestNetwork {

    private static final ResourceLocation GUI_CONTAINER_S2C = new ResourceLocation(TestMod.MOD_ID, "gui_container_s2c");
    private static final ResourceLocation GUI_CONTAINER_C2S = new ResourceLocation(TestMod.MOD_ID, "gui_container_c2s");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, GUI_CONTAINER_S2C, (buf, context) -> context.queue(() -> ModularGuiContainerMenu.handlePacketFromServer(context.getPlayer(), buf)));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, GUI_CONTAINER_C2S, (buf, context) -> context.queue(() -> ModularGuiContainerMenu.handlePacketFromClient(context.getPlayer(), buf)));
    }

    public static void sendContainerPacketToServer(Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToServer(GUI_CONTAINER_C2S, buf);
    }

    public static void sendContainerPacketToClient(ServerPlayer player, Consumer<FriendlyByteBuf> packetWriter) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packetWriter.accept(buf);
        NetworkManager.sendToPlayer(player, GUI_CONTAINER_S2C, buf);
    }
}
