package net.creeperhost.polylib.client.modulargui.lib;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Rectangle;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * This class primarily based on GuiHelper from BrandonsCore
 * But its implementation is heavily inspired by the new GuiGraphics system in 1.20+
 * <p>
 * The purpose of this class is to provide most of the basic rendering functions required to render various GUI geometry.
 * This includes things like simple rectangles, textures, strings, etc.
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public class GuiRender extends LegacyRender {
    public static final RenderType SOLID = RenderType.gui();

    private final Minecraft mc;
    private final PoseStack pose;
    private final MultiBufferSource.BufferSource buffers;
    private boolean batchDraw;

    public GuiRender(Minecraft mc, PoseStack poseStack, MultiBufferSource.BufferSource buffers) {
        this.mc = mc;
        this.pose = poseStack;
        this.buffers = buffers;
    }

    public GuiRender(Minecraft mc, MultiBufferSource.BufferSource buffers) {
        this(mc, new PoseStack(), buffers);
    }

    @Override
    public PoseStack pose() {
        return pose;
    }

    public MultiBufferSource.BufferSource buffers() {
        return buffers;
    }

    public Minecraft mc() {
        return mc;
    }

    /**
     * Allow similar render calls to be batched together into a single draw for better render efficiency.
     * All render calls in batch must use the same render type.
     *
     * @param batch callback in which the rendering should be implemented.
     */
    public void batchDraw(Runnable batch) {
        batchDraw = true;
        batch.run();
        batchDraw = false;
        flush();
    }

    private void flushIfUnBatched() {
        if (!this.batchDraw) this.flush();
    }

    private void flushIfBatched() {
        if (this.batchDraw) this.flush();
    }

    public void flush() {
        RenderSystem.disableDepthTest();
        this.buffers.endBatch();
        RenderSystem.enableDepthTest();
    }

    //=== Un-Textured geometry ===//

    /**
     * Fill rectangle with solid colour
     */
    public void rect(Rectangle rectangle, int colour) {
        this.rect(SOLID, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(RenderType type, Rectangle rectangle, int colour) {
        this.rect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(double x, double y, double width, double height, int colour) {
        this.fill(SOLID, x, y, x + width, y + height, colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(RenderType type, double x, double y, double width, double height, int colour) {
        this.fill(type, x, y, x + width, y + height, colour);
    }

    /**
     * Fill area with solid colour
     */
    public void fill(double xMin, double yMin, double xMax, double yMax, int colour) {
        this.fill(SOLID, xMin, yMin, xMax, yMax, colour);
    }

    /**
     * Fill area with solid colour
     */
    public void fill(RenderType type, double xMin, double yMin, double xMax, double yMax, int colour) {
        if (xMax < xMin) {
            double min = xMax;
            xMax = xMin;
            xMin = min;
        }
        if (yMax < yMin) {
            double min = yMax;
            yMax = yMin;
            yMin = min;
        }

        Matrix4f mat = pose.last().pose();
        VertexConsumer buffer = buffers.getBuffer(type);
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(colour).endVertex(); //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(colour).endVertex(); //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(colour).endVertex(); //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(colour).endVertex(); //L-B
        flushIfUnBatched();
    }

    /**
     * Fill area with colour gradient from top to bottom
     */
    public void gradientFillV(double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
        this.gradientFillV(SOLID, xMin, yMin, xMax, yMax, topColour, bottomColour);
    }

    /**
     * Fill area with colour gradient from top to bottom
     */
    public void gradientFillV(RenderType type, double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
        VertexConsumer buffer = buffers().getBuffer(type);
        float sA = (float) ARGB32.alpha(topColour) / 255.0F;
        float sR = (float) ARGB32.red(topColour) / 255.0F;
        float sG = (float) ARGB32.green(topColour) / 255.0F;
        float sB = (float) ARGB32.blue(topColour) / 255.0F;
        float eA = (float) ARGB32.alpha(bottomColour) / 255.0F;
        float eR = (float) ARGB32.red(bottomColour) / 255.0F;
        float eG = (float) ARGB32.green(bottomColour) / 255.0F;
        float eB = (float) ARGB32.blue(bottomColour) / 255.0F;
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(eR, eG, eB, eA).endVertex(); //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(sR, sG, sB, sA).endVertex(); //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(sR, sG, sB, sA).endVertex(); //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(eR, eG, eB, eA).endVertex(); //L-B
        this.flushIfUnBatched();
    }

    /**
     * Fill area with colour gradient from left to right
     */
    public void gradientFillH(double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
        this.gradientFillH(SOLID, xMin, yMin, xMax, yMax, leftColour, rightColour);
    }

    /**
     * Fill area with colour gradient from left to right
     */
    public void gradientFillH(RenderType type, double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
        VertexConsumer buffer = buffers().getBuffer(type);
        float sA = (float) ARGB32.alpha(leftColour) / 255.0F;
        float sR = (float) ARGB32.red(leftColour) / 255.0F;
        float sG = (float) ARGB32.green(leftColour) / 255.0F;
        float sB = (float) ARGB32.blue(leftColour) / 255.0F;
        float eA = (float) ARGB32.alpha(rightColour) / 255.0F;
        float eR = (float) ARGB32.red(rightColour) / 255.0F;
        float eG = (float) ARGB32.green(rightColour) / 255.0F;
        float eB = (float) ARGB32.blue(rightColour) / 255.0F;
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(eR, eG, eB, eA).endVertex(); //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(eR, eG, eB, eA).endVertex(); //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(sR, sG, sB, sA).endVertex(); //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(sR, sG, sB, sA).endVertex(); //L-B
        this.flushIfUnBatched();
    }

    /**
     * Draw a bordered rectangle of specified with specified border width, border colour and fill colour.
     */
    public void borderRect(double x, double y, double width, double height, double borderWidth, int fillColour, int borderColour) {
        borderFill(x, y, x + width, y + height, borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a bordered rectangle of specified with specified border width, border colour and fill colour.
     */
    public void borderRect(RenderType type, double x, double y, double width, double height, double borderWidth, int fillColour, int borderColour) {
        borderFill(type, x, y, x + width, y + height, borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a border of specified with, fill internal area with solid colour.
     */
    public void borderFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
        borderFill(SOLID, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a border of specified with, fill internal area with solid colour.
     */
    public void borderFill(RenderType type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
        //Draw batched for efficiency, unless already doing a batch draw.
        if (batchDraw) {
            borderFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour);
        } else {
            batchDraw(() -> borderFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour));
        }
    }

    private void borderFillInternal(RenderType type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
        fill(type, xMin, yMin, xMax, yMin + borderWidth, borderColour);                                             //Top
        fill(type, xMin, yMin + borderWidth, xMin + borderWidth, yMax - borderWidth, borderColour);                 //Left
        fill(type, xMin, yMax - borderWidth, xMax, yMax, borderColour);                                             //Bottom
        fill(type, xMax - borderWidth, yMin + borderWidth, xMax, yMax - borderWidth, borderColour);                 //Right
        if (fillColour != 0) //No point rendering fill if there is no fill colour
            fill(type, xMin + borderWidth, yMin + borderWidth, xMax - borderWidth, yMax - borderWidth, fillColour); //Fill
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedFill(SOLID, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, midColour(topLeftColour, bottomRightColour), fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(SOLID, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(RenderType type, double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(type, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedFill(SOLID, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, midColour(topLeftColour, bottomRightColour), fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(SOLID, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(RenderType type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        //Draw batched for efficiency, unless already doing a batch draw.
        if (batchDraw) {
            shadedFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
        } else {
            batchDraw(() -> shadedFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour));
        }
    }

    public void shadedFillInternal(RenderType type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        fill(type, xMin, yMin, xMax - borderWidth, yMin + borderWidth, topLeftColour);                               //Top
        fill(type, xMin, yMin + borderWidth, xMin + borderWidth, yMax - borderWidth, topLeftColour);                 //Left
        fill(type, xMin + borderWidth, yMax - borderWidth, xMax, yMax, bottomRightColour);                           //Bottom
        fill(type, xMax - borderWidth, yMin + borderWidth, xMax, yMax - borderWidth, bottomRightColour);             //Right
        fill(type, xMax - borderWidth, yMin, xMax, yMin + borderWidth, cornerMixColour);                             //Top Right Corner
        fill(type, xMin, yMax - borderWidth, xMin + borderWidth, yMax, cornerMixColour);                             //Bottom Left Corner

        if (fillColour != 0) //No point rendering fill if there is no fill colour
            fill(type, xMin + borderWidth, yMin + borderWidth, xMax - borderWidth, yMax - borderWidth, fillColour);  //Fill
    }

    //=== Textured geometry ===//

    //Sprite plus RenderType

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, TextureAtlasSprite sprite) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, TextureAtlasSprite sprite) {
        spriteRect(type, x, y, width, height, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, x, y, width, height, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        sprite(type, x, x + width, y, y + height, sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, TextureAtlasSprite sprite) {
        sprite(type, xMin, xMax, yMin, yMax, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, TextureAtlasSprite sprite, int argb) {
        sprite(type, xMin, xMax, yMin, yMax, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        VertexConsumer buffer = buffers().getBuffer(type);
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(red, green, blue, alpha).uv(sprite.getU1(), sprite.getV1()).endVertex();  //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(red, green, blue, alpha).uv(sprite.getU1(), sprite.getV0()).endVertex();  //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(red, green, blue, alpha).uv(sprite.getU0(), sprite.getV0()).endVertex();  //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(red, green, blue, alpha).uv(sprite.getU0(), sprite.getV1()).endVertex();  //L-B
        flushIfUnBatched();
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite) {
        spriteRect(type, x, y, width, height, rotation, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, x, y, width, height, rotation, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void spriteRect(RenderType type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        sprite(type, x, x + width, y, y + height, rotation, sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, int rotation, TextureAtlasSprite sprite) {
        sprite(type, xMin, xMax, yMin, yMax, rotation, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, int rotation, TextureAtlasSprite sprite, int argb) {
        sprite(type, xMin, xMax, yMin, yMax, rotation, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void sprite(RenderType type, double xMin, double xMax, double yMin, double yMax, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        float[] u = {sprite.getU0(), sprite.getU1(), sprite.getU1(), sprite.getU0()};
        float[] v = {sprite.getV1(), sprite.getV1(), sprite.getV0(), sprite.getV0()};
        VertexConsumer buffer = buffers().getBuffer(type);
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]).endVertex();  //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]).endVertex();  //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]).endVertex();  //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]).endVertex();  //L-B
        flushIfUnBatched();
    }

    //Material

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, Rectangle rectangle) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, Rectangle rectangle, int argb) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, Rectangle rectangle, float red, float green, float blue, float alpha) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, double x, double y, double width, double height) {
        texRect(material, x, y, width, height, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, double x, double y, double width, double height, int argb) {
        texRect(material, x, y, width, height, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void texRect(Material material, double x, double y, double width, double height, float red, float green, float blue, float alpha) {
        tex(material, x, x + width, y, y + height, red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double xMax, double yMin, double yMax) {
        tex(material, xMin, xMax, yMin, yMax, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double xMax, double yMin, double yMax, int argb) {
        tex(material, xMin, xMax, yMin, yMax, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double xMax, double yMin, double yMax, float red, float green, float blue, float alpha) {
        TextureAtlasSprite sprite = material.sprite();
        VertexConsumer buffer = material.buffer(buffers, GuiRender::texColType);
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(red, green, blue, alpha).uv(sprite.getU1(), sprite.getV1()).endVertex();  //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(red, green, blue, alpha).uv(sprite.getU1(), sprite.getV0()).endVertex();  //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(red, green, blue, alpha).uv(sprite.getU0(), sprite.getV0()).endVertex();  //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(red, green, blue, alpha).uv(sprite.getU0(), sprite.getV1()).endVertex();  //L-B
        flushIfUnBatched();
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, int rotation, Rectangle rectangle) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, int rotation, Rectangle rectangle, int argb) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, int rotation, Rectangle rectangle, float red, float green, float blue, float alpha) {
        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, int rotation, double x, double y, double width, double height) {
        texRect(material, x, y, width, height, rotation, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, int rotation, double x, double y, double width, double height, int argb) {
        texRect(material, x, y, width, height, rotation, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void texRect(Material material, double x, double y, double width, double height, int rotation, float red, float green, float blue, float alpha) {
        tex(material, x, x + width, y, y + height, rotation, red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void tex(Material material, int rotation, double xMin, double xMax, double yMin, double yMax) {
        tex(material, xMin, xMax, yMin, yMax, rotation, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void tex(Material material, double xMin, double xMax, double yMin, double yMax, int rotation, int argb) {
        tex(material, xMin, xMax, yMin, yMax, rotation, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     *
     * @param rotation Rotates sprite clockwise in 90 degree steps.
     */
    public void tex(Material material, double xMin, double xMax, double yMin, double yMax, int rotation, float red, float green, float blue, float alpha) {
        TextureAtlasSprite sprite = material.sprite();
        VertexConsumer buffer = material.buffer(buffers, GuiRender::texColType);
        float[] u = {sprite.getU0(), sprite.getU1(), sprite.getU1(), sprite.getU0()};
        float[] v = {sprite.getV1(), sprite.getV1(), sprite.getV0(), sprite.getV0()};
        Matrix4f mat = pose.last().pose();
        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]).endVertex();  //R-B
        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]).endVertex();  //R-T
        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]).endVertex();  //L-T
        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]).endVertex();  //L-B
        flushIfUnBatched();
    }

    //Slice and stitch

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    private void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int argb) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    private void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, 1F, 1F, 1F, 1F);
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders. 
     */
    private void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, float red, float green, float blue, float alpha) {
        if (batchDraw) {//Draw batched for efficiency, unless already doing a batch draw.
            dynamicTexInternal(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, red, green, blue, alpha);
        } else {
            batchDraw(() -> dynamicTexInternal(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, red, green, blue, alpha));
        }
    }

    //Todo, This method can probably be made a lot more efficient.
    // Also thinking a about a data generator to automatically stitch together these textures so there is no need to generate them dynamically.
    // That would be a lot more efficient and more compatible with custom resource packs.
    private void dynamicTexInternal(Material material, int xPos, int yPos, int xSize, int ySize, int topBorder, int leftBorder, int bottomBorder, int rightBorder, float red, float green, float blue, float alpha) {
        TextureAtlasSprite sprite = material.sprite();
        VertexConsumer buffer = material.buffer(buffers, GuiRender::texColType);        
        SpriteContents contents = sprite.contents();
        int texWidth = contents.width();
        int texHeight = contents.height();
        int trimWidth = texWidth - leftBorder - rightBorder;
        int trimHeight = texHeight - topBorder - bottomBorder;
        if (xSize <= texWidth) trimWidth = Math.min(trimWidth, xSize - rightBorder);
        if (xSize <= 0 || ySize <= 0 || trimWidth <= 0 || trimHeight <= 0) return;

        for (int x = 0; x < xSize; ) {
            int rWidth = Math.min(xSize - x, trimWidth);
            int trimU = 0;
            if (x != 0) {
                if (x + leftBorder + trimWidth <= xSize) {
                    trimU = leftBorder;
                } else {
                    trimU = (texWidth - (xSize - x));
                }
            }

            //Top & Bottom trim
            bufferDynamic(buffer, sprite, xPos + x, yPos, trimU, 0, rWidth, topBorder, red, green, blue, alpha);
            bufferDynamic(buffer, sprite, xPos + x, yPos + ySize - bottomBorder, trimU, texHeight - bottomBorder, rWidth, bottomBorder, red, green, blue, alpha);

            rWidth = Math.min(xSize - x - leftBorder - rightBorder, trimWidth);
            for (int y = 0; y < ySize; ) {
                int rHeight = Math.min(ySize - y - topBorder - bottomBorder, trimHeight);
                int trimV;
                if (y + (texHeight - topBorder - bottomBorder) <= ySize) {
                    trimV = topBorder;
                } else {
                    trimV = texHeight - (ySize - y);
                }

                //Left & Right trim
                if (x == 0 && y + topBorder < ySize - bottomBorder) {
                    bufferDynamic(buffer, sprite, xPos, yPos + y + topBorder, 0, trimV, leftBorder, rHeight, red, green, blue, alpha);
                    bufferDynamic(buffer, sprite, xPos + xSize - rightBorder, yPos + y + topBorder, trimU + texWidth - rightBorder, trimV, rightBorder, rHeight, red, green, blue, alpha);
                }

                //Core
                if (y + topBorder < ySize - bottomBorder && x + leftBorder < xSize - rightBorder) {
                    bufferDynamic(buffer, sprite, xPos + x + leftBorder, yPos + y + topBorder, leftBorder, topBorder, rWidth, rHeight, red, green, blue, alpha);
                }
                y += trimHeight;
            }
            x += trimWidth;
        }
    }

    private void bufferDynamic(VertexConsumer builder, TextureAtlasSprite tex, int x, int y, double textureX, double textureY, int width, int height, float red, float green, float blue, float alpha) {
        int w = tex.contents().width();
        int h = tex.contents().height();
        //@formatter:off
        builder.vertex(x,         y + height, 0).color(red, green, blue, alpha).uv(tex.getU((textureX / w) * 16D),          tex.getV(((textureY + height) / h) * 16)).endVertex();
        builder.vertex(x + width, y + height, 0).color(red, green, blue, alpha).uv(tex.getU(((textureX + width) / w) * 16), tex.getV(((textureY + height) / h) * 16)).endVertex();
        builder.vertex(x + width, y,          0).color(red, green, blue, alpha).uv(tex.getU(((textureX + width) / w) * 16), tex.getV(((textureY) / h) * 16)).endVertex();
        builder.vertex(x,         y,          0).color(red, green, blue, alpha).uv(tex.getU((textureX / w) * 16),           tex.getV(((textureY) / h) * 16)).endVertex();
        //@formatter:on
    }




//    buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(eR, eG, eB, eA).endVertex(); //R-B
//    buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(eR, eG, eB, eA).endVertex(); //R-T
//    buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(sR, sG, sB, sA).endVertex(); //L-T
//    buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(sR, sG, sB, sA).endVertex(); //L-B

    //=== Strings ===//
    //=== ItemStacks ===//
    //=== Entity? ===//
    //=== Hover Text ===//


    //=== Render Utils ===//

    //Push/Pop scissor
    //Set Colour


    //=== Static Utils ===//

    public static boolean isInRect(double minX, double minY, double width, double height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    public static boolean isInRect(int minX, int minY, int width, int height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    /**
     * Mixes the two input colours by adding up the R, G, B and A values of each input.
     */
    public static int mixColours(int colour1, int colour2) {
        return mixColours(colour1, colour2, false);
    }

    /**
     * Mixes the two input colours by adding up the R, G, B and A values of each input.
     *
     * @param subtract If true, subtract colour2 from colour1, otherwise add colour2 to colour1.
     */
    public static int mixColours(int colour1, int colour2, boolean subtract) {
        int alpha1 = colour1 >> 24 & 255;
        int alpha2 = colour2 >> 24 & 255;
        int red1 = colour1 >> 16 & 255;
        int red2 = colour2 >> 16 & 255;
        int green1 = colour1 >> 8 & 255;
        int green2 = colour2 >> 8 & 255;
        int blue1 = colour1 & 255;
        int blue2 = colour2 & 255;

        int alpha = Mth.clamp(alpha1 + (subtract ? -alpha2 : alpha2), 0, 255);
        int red = Mth.clamp(red1 + (subtract ? -red2 : red2), 0, 255);
        int green = Mth.clamp(green1 + (subtract ? -green2 : green2), 0, 255);
        int blue = Mth.clamp(blue1 + (subtract ? -blue2 : blue2), 0, 255);

        return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    /**
     * Returns a colour half-way between the two input colours.
     * The R, G, B and A channels are extracted from each input,
     * Then for each chanel, a midpoint is determined,
     * And a new colour is constructed based on the midpoint of each channel.
     */
    public static int midColour(int colour1, int colour2) {
        int alpha1 = colour1 >> 24 & 255;
        int alpha2 = colour2 >> 24 & 255;
        int red1 = colour1 >> 16 & 255;
        int red2 = colour2 >> 16 & 255;
        int green1 = colour1 >> 8 & 255;
        int green2 = colour2 >> 8 & 255;
        int blue1 = colour1 & 255;
        int blue2 = colour2 & 255;
        return (alpha2 + (alpha1 - alpha2) / 2 & 0xFF) << 24 | (red2 + (red1 - red2) / 2 & 0xFF) << 16 | (green2 + (green1 - green2) / 2 & 0xFF) << 8 | blue2 + (blue1 - blue2) / 2 & 0xFF;
    }

    private static float r(int argb) {
        return ARGB32.red(argb) / 255F;
    }

    private static float g(int argb) {
        return ARGB32.green(argb) / 255F;

    }

    private static float b(int argb) {
        return ARGB32.blue(argb) / 255F;

    }

    private static float a(int argb) {
        return ARGB32.alpha(argb) / 255F;

    }

    //Render Type Builders

    public static RenderType texType(ResourceLocation location) {
        return RenderType.create("tex_type", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false));
    }

    public static RenderType texColType(ResourceLocation location) {
        return RenderType.create("tex_col_type", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false));
    }
}
