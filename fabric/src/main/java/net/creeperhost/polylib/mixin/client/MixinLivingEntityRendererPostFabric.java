package net.creeperhost.polylib.mixin.client;

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
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_LIVING_POST},
 * {@link PolyEntityRenderEvents#RENDER_PLAYER_POST}.
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRendererPostFabric
{
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("RETURN"))
    private void polylib$onLivingSubmitPost(LivingEntityRenderState renderState,
                                            PoseStack poseStack,
                                            SubmitNodeCollector collector,
                                            CameraRenderState cameraState,
                                            CallbackInfo ci)
    {
        PolyEntityRenderEvents.RENDER_LIVING_POST.invoker()
                .onRenderLivingPost(renderState, poseStack, 0f);
        if (renderState instanceof AvatarRenderState avatarState)
        {
            PolyEntityRenderEvents.RENDER_PLAYER_POST.invoker()
                    .onRenderPlayerPost(avatarState, poseStack, 0f);
        }
    }
}
