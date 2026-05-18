package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.chat.client.tab.*;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreenNF {

    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void polylib$redirectChatInput(String msg, boolean addToRecent, CallbackInfo ci) {
        ChatTabRegistry reg = ChatTabRegistry.get();
        if (!reg.hasAnyChannel()) return;

        ChatTab active = reg.getActiveTab();
        switch (active) {
            case VanillaTab v -> {}
            case AllTab a -> {
                for (var entry : reg.allEntries()) {
                    entry.channel().submitMessage(msg);
                }
            }
            case ChannelTab ct -> {
                ct.channel().submitMessage(msg);
                ci.cancel();
            }
            case NotificationTab nt -> ci.cancel(); // notifications are read-only
        }
    }
}
