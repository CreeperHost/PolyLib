package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class MixinToastAddFabric {

    @Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
    private void onAddToast(Toast toast, CallbackInfo ci) {
        net.creeperhost.polylib.event.CancelContext ctx = new net.creeperhost.polylib.event.CancelContext();
        PolyGuiEvents.TOAST_ADD.invoker().onToastAdd(toast, ctx);
        if (ctx.isCancelled()) {
            ci.cancel();
        }
    }
}
