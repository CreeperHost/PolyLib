package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyInputEvents#INPUT_KEY}.
 * Injects into the key event handler called by the SDL event loop.
 */
@Mixin(KeyboardHandler.class)
public abstract class FabricKeyInputMixin
{
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void polylib$onKeyCallback(long windowPtr, int action, KeyEvent event, CallbackInfo ci)
    {
        PolyInputEvents.INPUT_KEY.invoker().onKeyInput(event.key(), event.keycode(), action, event.modifiers());
    }
}
