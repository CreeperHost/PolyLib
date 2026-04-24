package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_ITEM_IN_FRAME}.
 * Injects at the head of {@code ItemFrameRenderer#submit}.
 */
@Mixin(ItemFrameRenderer.class)
public abstract class MixinItemFrameRendererFabric
{
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onItemFrameSubmit(ItemFrameRenderState renderState,
                                           PoseStack poseStack,
                                           SubmitNodeCollector collector,
                                           CameraRenderState cameraState,
                                           CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.RENDER_ITEM_IN_FRAME.invoker()
                .onRenderItemInFrame(renderState.item, renderState, poseStack, ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
