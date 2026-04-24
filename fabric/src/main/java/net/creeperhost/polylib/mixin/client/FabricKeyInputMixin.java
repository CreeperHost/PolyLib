package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyInputEvents#INPUT_KEY}.
 * Injects into the GLFW key callback registered by {@code KeyboardHandler#setup}.
 */
@Mixin(KeyboardHandler.class)
public abstract class FabricKeyInputMixin
{
    @Inject(method = "lambda$setup$0", at = @At("HEAD"), remap = false)
    private void polylib$onKeyCallback(long windowPtr, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
    {
        PolyInputEvents.INPUT_KEY.invoker().onKeyInput(key, scanCode, action, modifiers);
    }
}
