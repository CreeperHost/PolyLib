package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_NAME_TAG}.
 * Injects at the head of {@code EntityRenderer#submitNameDisplay}.
 */
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRendererNameTagFabric
{
    @Inject(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onSubmitNameDisplay(EntityRenderState renderState,
                                             PoseStack poseStack,
                                             SubmitNodeCollector collector,
                                             CameraRenderState cameraState,
                                             CallbackInfo ci)
    {
        // We don't have the Component at this call site — pass empty as fallback.
        // NeoForge provides the actual name content via RenderNameTagEvent.DoRender.
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.RENDER_NAME_TAG.invoker()
                .onRenderNameTag(renderState, Component.empty(), poseStack, ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
