package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_HAND}.
 * Injects at the head of {@code ItemInHandRenderer#renderArmWithItem} (private method).
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRendererHandFabric
{
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void polylib$onRenderArmWithItem(AbstractClientPlayer player,
                                              float partialTick,
                                              float equipProgress,
                                              InteractionHand hand,
                                              float swingProgress,
                                              ItemStack itemStack,
                                              float bob,
                                              PoseStack poseStack,
                                              SubmitNodeCollector collector,
                                              int packedLight,
                                              CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.RENDER_HAND.invoker()
                .onRenderHand(hand, itemStack, poseStack, partialTick, ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
