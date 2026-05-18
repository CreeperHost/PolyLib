package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.chat.client.tab.ChatTabRegistry;
import net.creeperhost.polylib.chat.client.tab.VanillaTab;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class MixinChatComponentNF {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$suppressVanillaChatRender(
            GuiGraphicsExtractor graphics, Font font, int ticks, int mouseX, int mouseY,
            ChatComponent.DisplayMode displayMode, boolean changeCursor,
            CallbackInfo ci) {
        ChatTabRegistry reg = ChatTabRegistry.get();
        if (reg.hasAnyChannel() && !(reg.getActiveTab() instanceof VanillaTab)) {
            ci.cancel();
        }
    }
}
