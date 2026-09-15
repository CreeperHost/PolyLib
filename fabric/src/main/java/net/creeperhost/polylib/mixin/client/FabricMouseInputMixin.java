package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyInputEvents#INPUT_MOUSE}.
 * Injects into the mouse button handler called by the SDL event loop.
 */
@Mixin(MouseHandler.class)
public abstract class FabricMouseInputMixin
{
    @Inject(method = "onButton", at = @At("HEAD"))
    private void polylib$onMouseButtonCallback(long windowPtr, MouseButtonInfo button, int action, CallbackInfo ci)
    {
        PolyInputEvents.INPUT_MOUSE.invoker().onMouseInput(button.button(), action, button.modifiers());
    }
}
