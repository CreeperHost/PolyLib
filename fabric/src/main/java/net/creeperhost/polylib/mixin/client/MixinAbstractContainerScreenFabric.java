package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreenFabric {

    //TODO
//    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
//    private void onRenderBg(GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
//        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
//        PolyGuiEvents.CONTAINER_SCREEN_RENDER_BG.invoker().onContainerScreenRenderBg(screen, GuiGraphicsExtractor, mouseX, mouseY);
//    }
//
//    @Inject(method = "renderLabels", at = @At("HEAD"))
//    private void onRenderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, CallbackInfo ci) {
//        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
//        PolyGuiEvents.CONTAINER_SCREEN_RENDER_FG.invoker().onContainerScreenRenderFg(screen, GuiGraphicsExtractor, mouseX, mouseY);
//    }
}
