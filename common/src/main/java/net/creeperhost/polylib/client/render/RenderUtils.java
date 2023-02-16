package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.creeperhost.polylib.client.model.Model3D;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Iterator;
import java.util.List;

public class RenderUtils
{
    public static void renderObject(Model3D object, PoseStack matrix, VertexConsumer buffer, int argb, int light)
    {
        if (object != null)
        {
            RenderResizableCuboid.INSTANCE.renderCube(object, matrix, buffer, argb, light);
        }
    }

    public static int calculateGlowLight(int light, FluidStack fluid)
    {
        return fluid.isEmpty() ? light : calculateGlowLight(light, 1);
    }

    public static final int FULL_LIGHT = 0xF000F0;

    public static int calculateGlowLight(int light, int glow)
    {
        return FULL_LIGHT;
    }

    public static int getColorARGB(FluidStack fluidStack, float fluidScale)
    {
        if (fluidStack.isEmpty())
        {
            return -1;
        }
        return getColorARGB(fluidStack);
    }

    private static int getColorARGB(FluidStack fluidStack)
    {
        return FluidStackHooks.getColor(fluidStack);
    }

    public static float getRed(int color)
    {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    public static float getGreen(int color)
    {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    public static float getBlue(int color)
    {
        return (color & 0xFF) / 255.0F;
    }

    public static float getAlpha(int color)
    {
        return (color >> 24 & 0xFF) / 255.0F;
    }

    public static void renderTooltipBox(PoseStack poseStack, int x, int y, int width, int height)
    {
        poseStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = poseStack.last().pose();
        fillGradient(matrix4f, bufferBuilder, x - 3, y - 4, x + width + 3, y - 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, x - 3, y + height + 3, x + width + 3, y + height + 4, 400, -267386864,
                -267386864);
        fillGradient(matrix4f, bufferBuilder, x - 3, y - 3, x + width + 3, y + height + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, x - 4, y - 3, x - 3, y + height + 3, 400, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, x + width + 3, y - 3, x + width + 4, y + height + 3, 400, -267386864,
                -267386864);
        fillGradient(matrix4f, bufferBuilder, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, 400, 1347420415,
                1344798847);
        fillGradient(matrix4f, bufferBuilder, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, 400,
                1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, x - 3, y - 3, x + width + 3, y - 3 + 1, 400, 1347420415, 1347420415);
        fillGradient(matrix4f, bufferBuilder, x - 3, y + height + 2, x + width + 3, y + height + 3, 400, 1344798847,
                1344798847);
        //        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(
                Tesselator.getInstance().getBuilder());
        poseStack.translate(0.0, 0.0, 400.0);

        bufferSource.endBatch();
        poseStack.popPose();
    }

    public static void fillGradient(Matrix4f matrix4f, BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o)
    {
        float f = (float) (n >> 24 & 255) / 255.0F;
        float g = (float) (n >> 16 & 255) / 255.0F;
        float h = (float) (n >> 8 & 255) / 255.0F;
        float p = (float) (n & 255) / 255.0F;
        float q = (float) (o >> 24 & 255) / 255.0F;
        float r = (float) (o >> 16 & 255) / 255.0F;
        float s = (float) (o >> 8 & 255) / 255.0F;
        float t = (float) (o & 255) / 255.0F;
        bufferBuilder.vertex(matrix4f, (float) k, (float) j, (float) m).color(g, h, p, f).endVertex();
        bufferBuilder.vertex(matrix4f, (float) i, (float) j, (float) m).color(g, h, p, f).endVertex();
        bufferBuilder.vertex(matrix4f, (float) i, (float) l, (float) m).color(r, s, t, q).endVertex();
        bufferBuilder.vertex(matrix4f, (float) k, (float) l, (float) m).color(r, s, t, q).endVertex();
    }
}
