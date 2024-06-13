package net.creeperhost.polylib.client.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class ScreenFluidRenderer
{
    public static final ScreenFluidRenderer INSTANCE = new ScreenFluidRenderer(1000, 16, 16, 16);

    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;

    private final int capacityMb;
    private final int width;
    private final int height;
    private final int minHeight;

    public ScreenFluidRenderer(int capacityMb, int width, int height, int minHeight)
    {
        this.capacityMb = capacityMb;
        this.width = width;
        this.height = height;
        this.minHeight = minHeight;
    }

    public void render(final int xPosition, final int yPosition, @NotNull FluidStack fluidStack)
    {
        drawFluid(xPosition, yPosition, fluidStack);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void drawFluid(final int xPosition, final int yPosition, @NotNull FluidStack fluidStack)
    {
        if (fluidStack.isEmpty())
        {
            return;
        }

        Fluid fluid = fluidStack.getFluid();

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

        int fluidColor = FluidStackHooks.getColor(fluid);

        int amount = (int) fluidStack.getAmount();
        int scaledAmount = (amount * height) / capacityMb;
        if (amount > 0 && scaledAmount < minHeight)
        {
            scaledAmount = minHeight;
        }
        if (scaledAmount > height)
        {
            scaledAmount = height;
        }

        drawTiledSprite(xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    private void drawTiledSprite(final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite)
    {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++)
        {
            for (int yTile = 0; yTile <= yTileCount; yTile++)
            {
                int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                int x = xPosition + (xTile * TEX_WIDTH);
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                if (width > 0 && height > 0)
                {
                    int maskTop = TEX_HEIGHT - height;
                    int maskRight = TEX_WIDTH - width;

                    drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack)
    {
        return FluidStackHooks.getStillTexture(fluidStack.getFluid());
    }

    public static void setGLColorFromInt(int color)
    {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    private static void drawTextureWithMasking(float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel)
    {
        double uMin = textureSprite.getU0();
        double uMax = textureSprite.getU1();
        double vMin = textureSprite.getV0();
        double vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(xCoord, yCoord + 16, zLevel).setUv((float) uMin, (float) vMax);
        bufferBuilder.addVertex(xCoord + 16 - maskRight, yCoord + 16, zLevel).setUv((float) uMax, (float) vMax);
        bufferBuilder.addVertex(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).setUv((float) uMax, (float) vMin);
        bufferBuilder.addVertex(xCoord, yCoord + maskTop, zLevel).setUv((float) uMin, (float) vMin);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }
}
