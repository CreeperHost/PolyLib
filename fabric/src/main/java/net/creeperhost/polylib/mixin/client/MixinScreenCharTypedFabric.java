package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyScreenEvents;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class MixinScreenCharTypedFabric {

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTypedPre(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Screen screen = (Screen) (Object) this;
        
        // Allow event (cancellable)
        net.creeperhost.polylib.event.data.CancelContext ctx = new net.creeperhost.polylib.event.data.CancelContext();
        PolyScreenEvents.SCREEN_CHAR_TYPED_ALLOW.invoker().onScreenCharTypedAllow(screen, codePoint, modifiers, ctx);
        if (ctx.isCancelled()) {
            cir.setReturnValue(false);
            return;
        }
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void onCharTypedPost(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Screen screen = (Screen) (Object) this;
        PolyScreenEvents.SCREEN_CHAR_TYPED_AFTER.invoker().onScreenCharTypedAfter(screen, codePoint, modifiers);
    }
}
