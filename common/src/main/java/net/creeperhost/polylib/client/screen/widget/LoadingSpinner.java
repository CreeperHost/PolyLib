package net.creeperhost.polylib.client.screen.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class LoadingSpinner
{
    public static void render(PoseStack poseStack, float partialTicks, int ticks, int x, int y, ItemStack stack)
    {
        int rotateTickMax = 60;
        int throbTickMax = 20;
        int rotateTicks = ticks % rotateTickMax;
        int throbTicks = ticks % throbTickMax;
        float rotationDegrees = (rotateTicks + partialTicks) * (360F / rotateTickMax);

        float scale = 1F + ((throbTicks >= (throbTickMax / 2) ? (throbTickMax - (throbTicks + partialTicks)) : (throbTicks + partialTicks)) * (2F / throbTickMax));
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1F);
        poseStack.mulPose(new Quaternionf().rotateLocalZ(rotationDegrees));
//        poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotationDegrees));
        drawItem(poseStack, stack, 0, true, null);
        poseStack.popPose();
    }

    public static void drawItem(PoseStack poseStack, ItemStack stack, int hash, boolean renderOverlay, @Nullable String text)
    {
        if (stack.isEmpty())
        {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(stack, null, mc.player, hash);

        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        // modelViewStack.translate(x, y, 100.0D + this.blitOffset);
        modelViewStack.scale(1F, -1F, 1F);
        modelViewStack.scale(16F, 16F, 16F);
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flatLight = !bakedModel.usesBlockLight();

        if (flatLight)
        {
            Lighting.setupForFlatItems();
        }



        itemRenderer.render(stack, ItemDisplayContext.GUI, false, new PoseStack(), bufferSource, 0xF000F0,
                OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();

        if (flatLight)
        {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();

        if (renderOverlay)
        {
            Tesselator t = Tesselator.getInstance();
            Font font = mc.font;

            if (stack.getCount() != 1 || text != null)
            {
                String s = text == null ? String.valueOf(stack.getCount()) : text;
                poseStack.pushPose();
                poseStack.translate(9D - font.width(s), 1D, 20D);
                font.drawInBatch(s, 0F, 0F, 0xFFFFFF, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
                bufferSource.endBatch();
                poseStack.popPose();
            }

            if (stack.isBarVisible())
            {
                RenderSystem.disableDepthTest();
                //TODO
//                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                int barWidth = stack.getBarWidth();
                int barColor = stack.getBarColor();
                draw(poseStack, t, -6, 5, 13, 2, 0, 0, 0, 255);
                draw(poseStack, t, -6, 5, barWidth, 1, barColor >> 16 & 255, barColor >> 8 & 255, barColor & 255, 255);
                RenderSystem.enableBlend();
                //TODO
//                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            float cooldown = mc.player == null ? 0F : mc.player.getCooldowns().getCooldownPercent(stack.getItem(),
                    mc.getFrameTime());

            if (cooldown > 0F)
            {
                RenderSystem.disableDepthTest();
                //TODO
//                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                draw(poseStack, t, -8, Mth.floor(16F * (1F - cooldown)) - 8, 16, Mth.ceil(16F * cooldown), 255, 255, 255, 127);
                //TODO
//                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
        }
    }

    private static void draw(PoseStack matrixStack, Tesselator t, int x, int y, int width, int height, int red, int green, int blue, int alpha)
    {
        if (width <= 0 || height <= 0)
        {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f m = matrixStack.last().pose();
        BufferBuilder renderer = t.getBuilder();
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderer.vertex(m, x, y, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x, y + height, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x + width, y + height, 0).color(red, green, blue, alpha).endVertex();
        renderer.vertex(m, x + width, y, 0).color(red, green, blue, alpha).endVertex();
        t.end();
    }
}
