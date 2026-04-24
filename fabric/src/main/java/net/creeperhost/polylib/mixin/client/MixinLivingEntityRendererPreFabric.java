package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_LIVING_PRE},
 * {@link PolyEntityRenderEvents#RENDER_PLAYER_PRE}.
 * <p>
 * Injects at the head of {@code LivingEntityRenderer#submit}. If any listener cancels
 * the context the render is skipped. When the render state is an
 * {@link AvatarRenderState}, also fires the player-specific events.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRendererPreFabric
{
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onLivingSubmitPre(LivingEntityRenderState renderState,
                                           PoseStack poseStack,
                                           SubmitNodeCollector collector,
                                           CameraRenderState cameraState,
                                           CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.RENDER_LIVING_PRE.invoker()
                .onRenderLivingPre(renderState, poseStack, 0f, ctx);
        if (!ctx.isCancelled() && renderState instanceof AvatarRenderState avatarState)
        {
            CancelContext playerCtx = new CancelContext();
            PolyEntityRenderEvents.RENDER_PLAYER_PRE.invoker()
                    .onRenderPlayerPre(avatarState, poseStack, 0f, playerCtx);
            if (playerCtx.isCancelled()) ctx.cancel();
        }
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
