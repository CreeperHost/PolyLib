package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyEntityRenderEvents;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityRenderEvents#RENDER_ARM}.
 * Injects at the head of {@code ItemInHandRenderer#renderPlayerArm} (private method).
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRendererArmFabric
{
    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true, remap = false)
    private void polylib$onRenderPlayerArm(PoseStack poseStack,
                                            SubmitNodeCollector collector,
                                            int packedLight,
                                            float partialTick,
                                            float bob,
                                            HumanoidArm arm,
                                            CallbackInfo ci)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player instanceof AbstractClientPlayer p ? p : null;
        if (player == null) return;
        CancelContext ctx = new CancelContext();
        PolyEntityRenderEvents.RENDER_ARM.invoker()
                .onRenderArm(arm, player, packedLight, poseStack, ctx);
        if (ctx.isCancelled())
        {
            ci.cancel();
        }
    }
}
