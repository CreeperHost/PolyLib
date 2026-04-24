package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyLevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyLevelRenderEvents#BEFORE_BLOCK_OUTLINE}.
 * <p>
 * Injects at the head of {@code LevelRenderer#extractBlockOutline}; if any listener
 * cancels the context the method returns early, suppressing the outline extraction.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererBlockOutlineNF
{
    @Inject(method = "extractBlockOutline", at = @At("HEAD"), cancellable = true)
    private void polylib$beforeBlockOutline(Camera camera, LevelRenderState state, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyLevelRenderEvents.BEFORE_BLOCK_OUTLINE.invoker().onBeforeBlockOutline(
                net.minecraft.client.Minecraft.getInstance(), ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
