package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyEntityRenderEvents#ALLOW_CAPE_RENDER}.
 * Injects at the head of {@link CapeLayer#submit} to allow listeners to suppress
 * cape rendering for a specific avatar.
 */
@Mixin(CapeLayer.class)
public abstract class MixinCapeLayerNF
{
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
            at = @At("HEAD"), cancellable = true)
    private void polylib$onCapeSubmit(PoseStack poseStack, SubmitNodeCollector collector,
                                      int packedLight, AvatarRenderState renderState,
                                      float partialTick, float bob, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.ALLOW_CAPE_RENDER.invoker().onAllowCapeRender(renderState, ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
