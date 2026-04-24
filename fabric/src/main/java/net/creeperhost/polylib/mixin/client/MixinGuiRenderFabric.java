package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyRenderEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGuiRenderFabric {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderPre(GuiGraphicsExtractor GuiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        PolyRenderEvents.RENDER_GUI_PRE.invoker().onRenderGui(GuiGraphicsExtractor, deltaTracker.getGameTimeDeltaTicks());
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderPost(GuiGraphicsExtractor GuiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        PolyRenderEvents.RENDER_GUI_POST.invoker().onRenderGui(GuiGraphicsExtractor, deltaTracker.getGameTimeDeltaTicks());
    }
}
