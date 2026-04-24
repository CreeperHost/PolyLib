package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyInputEvents;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyInputEvents#INPUT_MOUSE}.
 * Injects into the GLFW mouse button callback registered by {@code MouseHandler#setup}.
 */
@Mixin(MouseHandler.class)
public abstract class FabricMouseInputMixin
{
    @Inject(method = "lambda$setup$2", at = @At("HEAD"), remap = false)
    private void polylib$onMouseButtonCallback(long windowPtr, int button, int action, int modifiers, CallbackInfo ci)
    {
        PolyInputEvents.INPUT_MOUSE.invoker().onMouseInput(button, action, modifiers);
    }
}
