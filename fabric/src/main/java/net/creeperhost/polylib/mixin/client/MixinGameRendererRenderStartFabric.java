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
 * Fabric bridge for {@link PolyLevelRenderEvents#RENDER_FRAME_START}.
 * Fires at the very beginning of each call to {@code GameRenderer#render}.
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRendererRenderStartFabric
{
    @Inject(method = "render", at = @At("HEAD"))
    private void polylib$onRenderFrameStart(DeltaTracker tracker, boolean onScreen, CallbackInfo ci)
    {
        PolyLevelRenderEvents.RENDER_FRAME_START.invoker().onRenderFrameStart(Minecraft.getInstance());
    }
}
