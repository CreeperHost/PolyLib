package net.creeperhost.polylib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@Deprecated(forRemoval = true) //Most if not all of these methods now exist in GuiGraphics.
public class ScreenHelper
{
    public void renderHead(GuiGraphics guiGraphics, int x, int y)
    {
        guiGraphics.blit(new ResourceLocation("textures/entity/steve.png"), x, y - 2, 9, 9, 8.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        guiGraphics.blit(new ResourceLocation("textures/entity/steve.png"), x, y - 2, 9, 9, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();
    }

    public static void drawModalRectWithCustomSizedTextureFloat(Matrix4f matrix, float x, float y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x, y + height, 0.0F).uv(u * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.vertex(matrix, x + width, y + height, 0.0F).uv((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        bufferbuilder.vertex(matrix, x + width, y, 0.0F).uv((u + (float) width) * f, v * f1).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0.0F).uv(u * f, v * f1).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawContinuousTexturedBox(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;

        // Draw Border
        // Top Left
        drawTexturedModalRect(matrixStack, x, y, u, v, leftBorder, topBorder, zLevel);
        // Top Right
        drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v,
                rightBorder, topBorder, zLevel);
        // Bottom Left
        drawTexturedModalRect(matrixStack, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder,
                bottomBorder, zLevel);
        // Bottom Right
        drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + canvasHeight,
                u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

        for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
        {
            // Top Border
            drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v,
                    (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel);
            // Bottom Border
            drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight,
                    u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth),
                    bottomBorder, zLevel);

            // Throw in some filler for good measure
            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
                drawTexturedModalRect(matrixStack, x + leftBorder + (i * fillerWidth),
                        y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder,
                        (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight),
                        zLevel);
        }

        // Side Borders
        for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
        {
            // Left Border
            drawTexturedModalRect(matrixStack, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder,
                    (j == yPasses ? remainderHeight : fillerHeight), zLevel);
            // Right Border
            drawTexturedModalRect(matrixStack, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight),
                    u + leftBorder + fillerWidth, v + topBorder, rightBorder,
                    (j == yPasses ? remainderHeight : fillerHeight), zLevel);
        }
    }

    public static void drawTexturedModalRect(PoseStack matrixStack, int x, int y, int u, int v, int width, int height, float zLevel)
    {
        final float uScale = 1f / 0x100;
        final float vScale = 1f / 0x100;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder wr = tessellator.getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = matrixStack.last().pose();
        wr.vertex(matrix, x, y + height, zLevel).uv(u * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y + height, zLevel).uv((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.vertex(matrix, x + width, y, zLevel).uv((u + width) * uScale, (v * vScale)).endVertex();
        wr.vertex(matrix, x, y, zLevel).uv(u * uScale, (v * vScale)).endVertex();
        BufferUploader.drawWithShader(wr.end());
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(x, y + height, 0.0D).uv((u * f),
                ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.vertex(x + width, y + height, 0.0D).uv(((u + (float) uWidth) * f),
                ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.vertex(x + width, y, 0.0D).uv(((u + (float) uWidth) * f),
                (v * f1)).endVertex();
        bufferbuilder.vertex(x, y, 0.0D).uv((u * f), (v * f1)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
