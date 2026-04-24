package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;

@Mixin(Screenshot.class)
public class MixinScreenshotFabric {

    @Inject(method = "grab(Ljava/io/File;Ljava/lang/String;Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
    private static void onGrabScreenshot(File file, String string, com.mojang.blaze3d.pipeline.RenderTarget renderTarget, java.util.function.Consumer<Component> consumer, CallbackInfo ci) {
        // Screenshot event fires before the grab; NativeImage is not available at this injection point.
        net.creeperhost.polylib.event.data.CancelContext ctx = new net.creeperhost.polylib.event.data.CancelContext();
        PolyGuiEvents.SCREENSHOT.invoker().onScreenshot(file, ctx);
        if (ctx.isCancelled()) {
            ci.cancel();
        }
    }
}
