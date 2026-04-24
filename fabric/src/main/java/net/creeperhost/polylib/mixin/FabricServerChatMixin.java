package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyChatEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyChatEvents#SERVER_CHAT}.
 */
@Mixin(PlayerList.class)
public abstract class FabricServerChatMixin
{
    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onBroadcastChatMessage(
            net.minecraft.network.chat.PlayerChatMessage message, ServerPlayer player,
            net.minecraft.network.chat.ChatType.Bound bound, CallbackInfo ci)
    {
        Component[] msg = { message.decoratedContent() };
        CancelContext ctx = new CancelContext();
        PolyChatEvents.SERVER_CHAT.invoker().onServerChat(player, msg, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
