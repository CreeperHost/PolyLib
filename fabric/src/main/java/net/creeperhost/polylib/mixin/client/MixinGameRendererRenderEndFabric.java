package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyLevelRenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLevelRenderEvents#RENDER_FRAME_END}.
 * Fires at the end of each call to {@code GameRenderer#render}, after all
 * rendering has been submitted.
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRendererRenderEndFabric
{
    @Inject(method = "render", at = @At("RETURN"))
    private void polylib$onRenderFrameEnd(DeltaTracker tracker, boolean onScreen, CallbackInfo ci)
    {
        PolyLevelRenderEvents.RENDER_FRAME_END.invoker().onRenderFrameEnd(Minecraft.getInstance());
    }
}
