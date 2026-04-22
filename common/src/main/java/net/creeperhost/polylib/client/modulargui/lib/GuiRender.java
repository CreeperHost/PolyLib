package net.creeperhost.polylib.client.modulargui.lib;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Borders;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Rectangle;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2ic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class primarily based on GuiHelper from BrandonsCore
 * But its implementation is heavily inspired by the new GuiGraphics system in 1.20+
 * <p>
 * The purpose of this class is to provide most of the basic rendering functions required to render various GUI geometry.
 * This includes things like simple rectangles, textures, strings, etc.
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public class GuiRender {
    private final GuiGraphicsExtractor wrapped;
    //    public static final RenderType SOLID = create("gui", 786432, RenderPipelines.GUI, RenderType.CompositeState.builder().createCompositeState(false));
//
//    //Used for things like events that require the vanilla GuiGraphics
//    private final RenderWrapper renderWrapper;
//
//    private final Minecraft mc;
//    private final PoseStack pose;
//    private final ScissorHandler scissorHandler = new ScissorHandler();
//    private final MultiBufferSource.BufferSource buffers;
//    private final ItemStackRenderState stackRenderState = new ItemStackRenderState();
//    private boolean batchDraw;
    private Font fontOverride;

//    public GuiRender(Minecraft mc, PoseStack poseStack, MultiBufferSource.BufferSource buffers) {
//        this.mc = mc;
//        this.pose = poseStack;
//        this.buffers = buffers;
//        this.renderWrapper = new RenderWrapper(this);
//    }
//
//    public GuiRender(Minecraft mc, MultiBufferSource.BufferSource buffers) {
//        this(mc, new PoseStack(), buffers);
//    }

//    public static GuiRender convert(GuiGraphics graphics) {
//        return new GuiRender(Minecraft.getInstance(), graphics.pose(), graphics.bufferSource);
//    }


    public GuiRender(GuiGraphicsExtractor wrapped) {
        this.wrapped = wrapped;
    }

    public GuiGraphicsExtractor graphics() {
        return wrapped;
    }

    public Matrix3x2fStack pose() {
        return wrapped.pose();
    }

//    public MultiBufferSource.BufferSource buffers() {
//        return wrapped.;
//    }

    public Minecraft mc() {
        return wrapped.minecraft;
    }

    public Font font() {
        return fontOverride == null ? mc().font : fontOverride;
    }

    public int guiWidth() {
        return mc().getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return mc().getWindow().getGuiScaledHeight();
    }

    /**
     * Allows you to override the font renderer used for all text rendering.
     * Be sure to set the override back to null when you are finished using your custom font!
     *
     * @param font The font to use, or null to disable override.
     */
    public void overrideFont(@Nullable Font font) {
        this.fontOverride = font;
    }

//    /**
//     * Allow similar render calls to be batched together into a single draw for better render efficiency.
//     * All render calls in batch must use the same render type.
//     *
//     * @param batch callback in which the rendering should be implemented.
//     */
//    public void batchDraw(Runnable batch) {
//        flush();
//        batchDraw = true;
//        batch.run();
//        batchDraw = false;
//        flush();
//    }
//
//    private void flushIfUnBatched() {
//        if (!batchDraw) flush();
//    }
//
//    private void flushIfBatched() {
//        if (batchDraw) flush();
//    }
//
//    public void flush() {
//        buffers.endBatch();
//    }
//
//    public void drawSpecial(Consumer<MultiBufferSource> consumer) {
//        consumer.accept(this.buffers);
//        this.buffers.endBatch();
//    }
//
//    /**
//     * Only use this as a last resort! It may explode... Have fun!
//     *
//     * @return A Vanilla GuiGraphics instance that wraps this {@link GuiRender}
//     */
//    @Deprecated
//    public RenderWrapper guiGraphicsWrapper() {
//        return renderWrapper;
//    }

    //=== Un-Textured geometry ===//

    /**
     * Fill rectangle with solid colour
     */
    public void rect(Rectangle rectangle, int colour) {
        this.rect(RenderPipelines.GUI, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(RenderPipeline type, Rectangle rectangle, int colour) {
        this.rect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(double x, double y, double width, double height, int colour) {
        this.fill(RenderPipelines.GUI, x, y, x + width, y + height, colour);
    }

    /**
     * Fill rectangle with solid colour
     */
    public void rect(RenderPipeline type, double x, double y, double width, double height, int colour) {
        this.fill(type, x, y, x + width, y + height, colour);
    }

    /**
     * Fill area with solid colour
     */
    public void fill(double xMin, double yMin, double xMax, double yMax, int colour) {
        this.fill(RenderPipelines.GUI, xMin, yMin, xMax, yMax, colour);
    }

    /**
     * Fill area with solid colour
     */
    public void fill(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int colour) {
//        if (xMax < xMin) {
//            double min = xMax;
//            xMax = xMin;
//            xMin = min;
//        }
//        if (yMax < yMin) {
//            double min = yMax;
//            yMax = yMin;
//            yMin = min;
//        }
//
//        Matrix4f mat = pose.last().pose();
//        VertexConsumer buffer = buffers.getBuffer(type);
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(colour); //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(colour); //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(colour); //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(colour); //L-B
//        flushIfUnBatched();
        wrapped.fill(type, (int) xMin, (int) yMin, (int) xMax, (int) yMax, colour);
    }

    /**
     * Fill area with colour gradient from top to bottom
     */
    public void gradientFillV(double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
//        this.gradientFillV(RenderPipelines.GUI, xMin, yMin, xMax, yMax, topColour, bottomColour);
        wrapped.fillGradient((int) xMin, (int) yMin, (int) xMax, (int) yMax, topColour, bottomColour);
    }
//
//    /**
//     * Fill area with colour gradient from top to bottom
//     */
//    public void gradientFillV(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
//        VertexConsumer buffer = buffers().getBuffer(type);
//        float sA = (float) ARGB.alpha(topColour) / 255.0F;
//        float sR = (float) ARGB.red(topColour) / 255.0F;
//        float sG = (float) ARGB.green(topColour) / 255.0F;
//        float sB = (float) ARGB.blue(topColour) / 255.0F;
//        float eA = (float) ARGB.alpha(bottomColour) / 255.0F;
//        float eR = (float) ARGB.red(bottomColour) / 255.0F;
//        float eG = (float) ARGB.green(bottomColour) / 255.0F;
//        float eB = (float) ARGB.blue(bottomColour) / 255.0F;
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(eR, eG, eB, eA); //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(sR, sG, sB, sA); //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(sR, sG, sB, sA); //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(eR, eG, eB, eA); //L-B
//        this.flushIfUnBatched();
//    }
//
//    /**
//     * Fill area with colour gradient from left to right
//     */
//    public void gradientFillH(double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
//        this.gradientFillH(RenderPipelines.GUI, xMin, yMin, xMax, yMax, leftColour, rightColour);
//    }
//
//    /**
//     * Fill area with colour gradient from left to right
//     */
//    public void gradientFillH(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
//        VertexConsumer buffer = buffers().getBuffer(type);
//        float sA = (float) ARGB.alpha(leftColour) / 255.0F;
//        float sR = (float) ARGB.red(leftColour) / 255.0F;
//        float sG = (float) ARGB.green(leftColour) / 255.0F;
//        float sB = (float) ARGB.blue(leftColour) / 255.0F;
//        float eA = (float) ARGB.alpha(rightColour) / 255.0F;
//        float eR = (float) ARGB.red(rightColour) / 255.0F;
//        float eG = (float) ARGB.green(rightColour) / 255.0F;
//        float eB = (float) ARGB.blue(rightColour) / 255.0F;
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(eR, eG, eB, eA); //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(eR, eG, eB, eA); //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(sR, sG, sB, sA); //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(sR, sG, sB, sA); //L-B
//        this.flushIfUnBatched();
//    }

    /**
     * Draw a bordered rectangle of specified with specified border width, border colour and fill colour.
     */
    public void borderRect(Rectangle rectangle, double borderWidth, int fillColour, int borderColour) {
        borderFill(rectangle.x(), rectangle.y(), rectangle.xMax(), rectangle.yMax(), borderWidth, fillColour, borderColour);
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
    public void borderRect(RenderPipeline type, Rectangle rectangle, double borderWidth, int fillColour, int borderColour) {
        borderFill(type, rectangle.x(), rectangle.y(), rectangle.xMax(), rectangle.yMax(), borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a bordered rectangle of specified with specified border width, border colour and fill colour.
     */
    public void borderRect(RenderPipeline type, double x, double y, double width, double height, double borderWidth, int fillColour, int borderColour) {
        borderFill(type, x, y, x + width, y + height, borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a border of specified with, fill internal area with solid colour.
     */
    public void borderFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
        borderFill(RenderPipelines.GUI, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour);
    }

    /**
     * Draw a border of specified with, fill internal area with solid colour.
     */
    public void borderFill(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
//        if (batchDraw) { //Draw batched for efficiency, unless already doing a batch draw.
        borderFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour);
//        } else {
//            batchDraw(() -> borderFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, fillColour, borderColour));
//        }
    }

    private void borderFillInternal(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int fillColour, int borderColour) {
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
    public void shadedRect(Rectangle rectangle, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, rectangle.x(), rectangle.y(), rectangle.xMax(), rectangle.yMax(), borderWidth, topLeftColour, bottomRightColour, midColour(topLeftColour, bottomRightColour), fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, midColour(topLeftColour, bottomRightColour), fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(Rectangle rectangle, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, rectangle.x(), rectangle.y(), rectangle.xMax(), rectangle.yMax(), borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset rectangle. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedRect(RenderPipeline type, double x, double y, double width, double height, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(type, x, y, x + width, y + height, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, midColour(topLeftColour, bottomRightColour), fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        shadedFill(RenderPipelines.GUI, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
    }

    /**
     * Can be used to create the illusion of an inset / outset area. This is identical to the way inventory slots are rendered except in code rather than via a texture.
     * Example Usage: render.shadedFill(0, 0, 18, 18, 1, 0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b); //Renders a vanilla style inventory slot
     * This can also be used to render things like buttons that appear to actually "push in" when you press them.
     */
    public void shadedFill(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
//        if (batchDraw) { //Draw batched for efficiency, unless already doing a batch draw.
        shadedFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour);
//        } else {
//            batchDraw(() -> shadedFillInternal(type, xMin, yMin, xMax, yMax, borderWidth, topLeftColour, bottomRightColour, cornerMixColour, fillColour));
//        }
    }

    public void shadedFillInternal(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, double borderWidth, int topLeftColour, int bottomRightColour, int cornerMixColour, int fillColour) {
        fill(type, xMin, yMin, xMax - borderWidth, yMin + borderWidth, topLeftColour);                               //Top
        fill(type, xMin, yMin + borderWidth, xMin + borderWidth, yMax - borderWidth, topLeftColour);                 //Left
        fill(type, xMin + borderWidth, yMax - borderWidth, xMax, yMax, bottomRightColour);                           //Bottom
        fill(type, xMax - borderWidth, yMin + borderWidth, xMax, yMax - borderWidth, bottomRightColour);             //Right
        fill(type, xMax - borderWidth, yMin, xMax, yMin + borderWidth, cornerMixColour);                             //Top Right Corner
        fill(type, xMin, yMax - borderWidth, xMin + borderWidth, yMax, cornerMixColour);                             //Bottom Left Corner

        if (fillColour != 0) //No point rendering fill if there is no fill colour
            fill(type, xMin + borderWidth, yMin + borderWidth, xMax - borderWidth, yMax - borderWidth, fillColour);  //Fill
    }

    //=== Generic Backgrounds ===//

    /**
     * Draws a rectangle / background with a style matching vanilla tool tips.
     */
    public void toolTipBackground(double x, double y, double width, double height) {
        toolTipBackground(x, y, width, height, 0xF0100010, 0x505000FF, 0x5028007f);
    }

    /**
     * Draws a rectangle / background with a style matching vanilla tool tips.
     * Vanilla Default Colours: 0xF0100010, 0x505000FF, 0x5028007f
     */
    public void toolTipBackground(double x, double y, double width, double height, int backgroundColour, int borderColourTop, int borderColourBottom) {
        toolTipBackground(x, y, width, height, backgroundColour, backgroundColour, borderColourTop, borderColourBottom, false);
    }

    /**
     * Draws a rectangle / background with a style matching vanilla tool tips.
     * Vanilla Default Colours: 0xF0100010, 0xF0100010, 0x505000FF, 0x5028007f
     */
    public void toolTipBackground(double x, double y, double width, double height, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom, boolean empty) {
//        if (batchDraw) { //Draw batched for efficiency, unless already doing a batch draw.
        toolTipBackgroundInternal(x, y, x + width, y + height, backgroundColourTop, backgroundColourBottom, borderColourTop, borderColourBottom, false);
//        } else {
//            batchDraw(() -> toolTipBackgroundInternal(x, y, x + width, y + height, backgroundColourTop, backgroundColourBottom, borderColourTop, borderColourBottom, false));
//        }
    }

    private void toolTipBackgroundInternal(double xMin, double yMin, double xMax, double yMax, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom, boolean empty) {
        fill(xMin + 1, yMin, xMax - 1, yMin + 1, backgroundColourTop);                                      // Top
        fill(xMin + 1, yMax - 1, xMax - 1, yMax, backgroundColourBottom);                                   // Bottom
        gradientFillV(xMin, yMin + 1, xMin + 1, yMax - 1, backgroundColourTop, backgroundColourBottom);     // Left
        gradientFillV(xMax - 1, yMin + 1, xMax, yMax - 1, backgroundColourTop, backgroundColourBottom);     // Right
        if (!empty) {
            gradientFillV(xMin + 1, yMin + 1, xMax - 1, yMax - 1, backgroundColourTop, backgroundColourBottom);   // Fill
        }
        gradientFillV(xMin + 1, yMin + 1, xMin + 2, yMax - 1, borderColourTop, borderColourBottom);         // Left Accent
        gradientFillV(xMax - 2, yMin + 1, xMax - 1, yMax - 1, borderColourTop, borderColourBottom);         // Right Accent
        fill(xMin + 2, yMin + 1, xMax - 2, yMin + 2, borderColourTop);                                      // Top Accent
        fill(xMin + 2, yMax - 2, xMax - 2, yMax - 1, borderColourBottom);                                   // Bottom Accent
    }

    //=== Textured geometry ===//

    //Sprite plus RenderPipeline

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, Rectangle rectangle, TextureAtlasSprite sprite) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, Rectangle rectangle, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, Rectangle rectangle, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, TextureAtlasSprite sprite) {
        spriteRect(type, x, y, width, height, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, TextureAtlasSprite sprite, int argb) {
        spriteRect(type, x, y, width, height, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        sprite(type, x, y, x + width, y + height, sprite, red, green, blue, alpha);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite) {
        sprite(type, xMin, yMin, xMax, yMax, sprite, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, a Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, int argb) {
        sprite(type, xMin, yMin, xMax, yMax, sprite, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a TextureAtlasSprite using the given render type, a Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
//        VertexConsumer buffer = buffers().getBuffer(type);
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(sprite.getU1(), sprite.getV1());  //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(sprite.getU1(), sprite.getV0());  //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(sprite.getU0(), sprite.getV0());  //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(sprite.getU0(), sprite.getV1());  //L-B
//        flushIfUnBatched();
//        wrapped.blitSprite(type, sprite, (int) xMin, (int) yMin, (int) xMax, (int) yMax, ARGB.colorFromFloat(alpha, red, green, blue));
        wrapped.innerBlit(type, sprite.atlasLocation(), (int) xMin, (int) xMax, (int) yMin, (int) yMax, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), ARGB.colorFromFloat(alpha, red, green, blue));
    }

    //TODO Sprite rotation like this is not possible with vanillas rendering... What to do..........
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite) {
//        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite, int argb) {
//        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, Rectangle rectangle, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
//        spriteRect(type, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, sprite, red, green, blue, alpha);
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite) {
//        spriteRect(type, x, y, width, height, rotation, sprite, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite, int argb) {
//        spriteRect(type, x, y, width, height, rotation, sprite, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void spriteRect(RenderPipeline type, double x, double y, double width, double height, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
//        sprite(type, x, y, x + width, y + height, rotation, sprite, red, green, blue, alpha);
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite) {
//        sprite(type, xMin, yMin, xMax, yMax, rotation, sprite, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite, int argb) {
//        sprite(type, xMin, yMin, xMax, yMax, rotation, sprite, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void sprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
//        float[] u = {sprite.getU0(), sprite.getU1(), sprite.getU1(), sprite.getU0()};
//        float[] v = {sprite.getV1(), sprite.getV1(), sprite.getV0(), sprite.getV0()};
//        VertexConsumer buffer = buffers().getBuffer(type);
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]);  //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]);  //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]);  //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]);  //L-B
//        flushIfUnBatched();
//
////        wrapped.blitSprite(type, sprite, (int) xMin, (int) yMin, (int) xMax, (int) yMax, ARGB.colorFromFloat(alpha, red, green, blue));
//        wrapped.innerBlit(type, sprite.atlasLocation(), (int) xMin, (int) yMin, (int) xMax, (int) yMax, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), ARGB.colorFromFloat(alpha, red, green, blue));
//    }

    //Partial Sprite

    //TODO figure out if there is a way to make this work.
//    /**
//     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     * <p>
//     * This is similar to {@link #partialSprite(RenderPipeline, double, double, double, double, TextureAtlasSprite, float, float, float, float, int)}
//     * Except the input uv values are in texture coordinates. So to draw a full 16x16 sprite with this you would supply 0, 0, 16, 16
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void partialSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite, int texXMin, int texYMin, int texXMax, int texYMax, int argb) {
//        float width = sprite.contents().width();
//        float height = sprite.contents().height();
//        partialSprite(type, xMin, yMin, xMax, yMax, rotation, sprite, texXMin / width, texYMin / height, texXMax / width, texYMax / height, argb);
//    }
//

//    /**
//     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     * Valid input u/v value range is 0 to 1 [0, 0, 1, 1 would render the full sprite]
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void partialSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, int argb) {
//        partialSprite(type, xMin, yMin, xMax, yMax, rotation, sprite, uMin, vMin, uMax, vMax, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     * Valid input u/v value range is 0 to 1 [0, 0, 1, 1 would render the full sprite]
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void partialSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, int rotation, TextureAtlasSprite sprite, float left, float top, float right, float bottom, float red, float green, float blue, float alpha) {
//        VertexConsumer buffer = buffers().getBuffer(type);
//        Matrix4f mat = pose.last().pose();
//        rotation = Math.floorMod(rotation, 4);
//
//        float[] sub = {left, top, right, bottom};
//        left = sub[rotation % 4];
//        top = sub[(rotation + 1) % 4];
//        right = sub[(rotation + 2) % 4];
//        bottom = sub[(rotation + 3) % 4];
//
//        float ul = sprite.getU1() - sprite.getU0();
//        float vl = sprite.getV1() - sprite.getV0();
//        float u0 = sprite.getU0() + (left * ul);
//        float v0 = sprite.getV0() + (top * vl);
//        float u1 = sprite.getU0() + (right * ul);
//        float v1 = sprite.getV0() + (bottom * vl);
//        float[] u = {u0, u1, u1, u0};
//        float[] v = {v1, v1, v0, v0};
//        buffer.vertex(mat, (float) xMax, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]).endVertex();  //R-B
//        buffer.vertex(mat, (float) xMax, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]).endVertex();  //R-T
//        buffer.vertex(mat, (float) xMin, (float) yMin, 0).color(red, green, blue, alpha).uv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]).endVertex();  //L-T
//        buffer.vertex(mat, (float) xMin, (float) yMax, 0).color(red, green, blue, alpha).uv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]).endVertex();  //L-B
//        flushIfUnBatched();
//    }

    /**
     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     * <p>
     * This is similar to {@link #partialSprite(RenderPipeline, double, double, double, double, TextureAtlasSprite, float, float, float, float, int)}
     * Except the input uv values are in texture coordinates. So to draw a full 16x16 sprite with this you would supply 0, 0, 16, 16
     */
    public void partialSpriteTex(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, double texXMin, double texYMin, double texXMax, double texYMax, int argb) {
        partialSpriteTex(type, xMin, yMin, xMax, yMax, sprite, texXMin, texYMin, texXMax, texYMax, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     * <p>
     * This is similar to {@link #partialSprite(RenderPipeline, double, double, double, double, TextureAtlasSprite, float, float, float, float, int)}
     * Except the input uv values are in texture coordinates. So to draw a full 16x16 sprite with this you would supply 0, 0, 16, 16
     */
    public void partialSpriteTex(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, double texXMin, double texYMin, double texXMax, double texYMax, float red, float green, float blue, float alpha) {
        int width = sprite.contents().width();
        int height = sprite.contents().height();
        partialSprite(type, xMin, yMin, xMax, yMax, sprite, (float) texXMin / width, (float) texYMin / height, (float) texXMax / width, (float) texYMax / height, red, green, blue, alpha);
    }

    /**
     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     * Valid input u/v value range is 0 to 1 [0, 0, 1, 1 would render the full sprite]
     */
    public void partialSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, int argb) {
        partialSprite(type, xMin, yMin, xMax, yMax, sprite, uMin, vMin, uMax, vMax, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a subsection of a TextureAtlasSprite using the given render type, Vertex format should be POSITION_COLOR_TEX
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     * Valid input u/v value range is 0 to 1 [0, 0, 1, 1 would render the full sprite]
     */
    public void partialSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float uMin, float vMin, float uMax, float vMax, float red, float green, float blue, float alpha) {
//        VertexConsumer buffer = buffers().getBuffer(type);
//        Matrix4f mat = pose.last().pose();
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float ul = u1 - u0;
        float vl = v1 - v0;
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u0 + (uMax * ul), v0 + (vMax * vl));  //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u0 + (uMax * ul), v0 + (vMin * vl));  //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u0 + (uMin * ul), v0 + (vMin * vl));  //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u0 + (uMin * ul), v0 + (vMax * vl));  //L-B
//        flushIfUnBatched();
        wrapped.innerBlit(type, sprite.atlasLocation(), (int) xMin, (int) xMax, (int) yMin, (int) yMax, u0 + (uMin * ul), u0 + (uMax * ul), v0 + (vMin * vl), v0 + (vMax * vl), ARGB.colorFromFloat(alpha, red, green, blue));
    }

    /**
     * Draw a sprite tiled to fit the specified area.
     * Sprite is drawn from the top-left so sprite will be tiled right and down.
     */
    public void tileSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, int argb) {
        tileSprite(type, xMin, yMin, xMax, yMax, sprite, sprite.contents().width(), sprite.contents().height(), argb);
    }

    /**
     * Draw a sprite tiled to fit the specified area.
     * Sprite is drawn from the top-left so sprite will be tiled right and down.
     *
     * @param textureWidth  Set base width of the sprite texture in pixels
     * @param textureHeight Set base height of the sprite texture in pixels
     */
    public void tileSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int argb) {
        tileSprite(type, xMin, yMin, xMax, yMax, sprite, textureWidth, textureHeight, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draw a sprite tiled to fit the specified area.
     * Sprite is drawn from the top-left so sprite will be tiled right and down.
     */
    public void tileSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        tileSprite(type, xMin, yMin, xMax, yMax, sprite, sprite.contents().width(), sprite.contents().height(), red, green, blue, alpha);
    }

    /**
     * Draw a sprite tiled to fit the specified area.
     * Sprite is drawn from the top-left so sprite will be tiled right and down.
     *
     * @param textureWidth  Set base width of the sprite texture in pixels
     * @param textureHeight Set base height of the sprite texture in pixels
     */
    public void tileSprite(RenderPipeline type, double xMin, double yMin, double xMax, double yMax, TextureAtlasSprite sprite, int textureWidth, int textureHeight, float red, float green, float blue, float alpha) {
        double width = xMax - xMin;
        double height = yMax - yMin;
        if (width <= textureWidth && height <= textureHeight) {
            partialSprite(type, xMin, yMin, xMax, yMax, sprite, 0F, 0F, (float) width / textureWidth, (float) height / textureHeight, red, green, blue, alpha);
        } else {
            double xPos = xMin;
            do {
                double sectionWidth = Math.min(textureWidth, xMax - xPos);
                double uWidth = sectionWidth / textureWidth;
                double yPos = yMin;
                do {
                    double sectionHeight = Math.min(textureHeight, yMax - yPos);
                    double vWidth = sectionHeight / textureHeight;
                    partialSprite(type, xPos, yPos, xPos + sectionWidth, yPos + sectionHeight, sprite, 0, 0, (float) uWidth, (float) vWidth, red, green, blue, alpha);
                    yPos += textureHeight;
                } while (yPos < yMax);
                xPos += textureWidth;
            } while (xPos < xMax);
        }
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
        tex(material, x, y, x + width, y + height, red, green, blue, alpha);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double yMin, double xMax, double yMax) {
        tex(material, xMin, yMin, xMax, yMax, 1F, 1F, 1F, 1F);
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int argb) {
        tex(material, xMin, yMin, xMax, yMax, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * Draws a texture sprite derived from the provided material.
     * Texture will be resized / reshaped as appropriate to fit the defined area.
     */
    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, float red, float green, float blue, float alpha) {
        TextureAtlasSprite sprite = material.sprite();
//        VertexConsumer buffer = material.buffer(buffers, GuiRender::texColType);
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(sprite.getU1(), sprite.getV1());  //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(sprite.getU1(), sprite.getV0());  //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(sprite.getU0(), sprite.getV0());  //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(sprite.getU0(), sprite.getV1());  //L-B
//        flushIfUnBatched();
        sprite(guiTexPipe(), xMin, yMin, xMax, yMax, sprite, red, green, blue, alpha);
    }

    //TODO rotation not supported
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, int rotation, Rectangle rectangle) {
//        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, int rotation, Rectangle rectangle, int argb) {
//        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, int rotation, Rectangle rectangle, float red, float green, float blue, float alpha) {
//        texRect(material, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), rotation, red, green, blue, alpha);
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, int rotation, double x, double y, double width, double height) {
//        texRect(material, x, y, width, height, rotation, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, int rotation, double x, double y, double width, double height, int argb) {
//        texRect(material, x, y, width, height, rotation, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void texRect(Material material, double x, double y, double width, double height, int rotation, float red, float green, float blue, float alpha) {
//        tex(material, x, y, x + width, y + height, rotation, red, green, blue, alpha);
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void tex(Material material, int rotation, double xMin, double yMin, double xMax, double yMax) {
//        tex(material, xMin, yMin, xMax, yMax, rotation, 1F, 1F, 1F, 1F);
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int rotation, int argb) {
//        tex(material, xMin, yMin, xMax, yMax, rotation, r(argb), g(argb), b(argb), a(argb));
//    }
//
//    /**
//     * Draws a texture sprite derived from the provided material.
//     * Texture will be resized / reshaped as appropriate to fit the defined area.
//     *
//     * @param rotation Rotates sprite clockwise in 90 degree steps.
//     */
//    public void tex(Material material, double xMin, double yMin, double xMax, double yMax, int rotation, float red, float green, float blue, float alpha) {
//        TextureAtlasSprite sprite = material.sprite();
//        VertexConsumer buffer = material.buffer(buffers, GuiRender::texColType);
//        float[] u = {sprite.getU0(), sprite.getU1(), sprite.getU1(), sprite.getU0()};
//        float[] v = {sprite.getV1(), sprite.getV1(), sprite.getV0(), sprite.getV0()};
//        Matrix4f mat = pose.last().pose();
//        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u[(1 + rotation) % 4], v[(1 + rotation) % 4]);  //R-B
//        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u[(2 + rotation) % 4], v[(2 + rotation) % 4]);  //R-T
//        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(red, green, blue, alpha).setUv(u[(3 + rotation) % 4], v[(3 + rotation) % 4]);  //L-T
//        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(red, green, blue, alpha).setUv(u[(0 + rotation) % 4], v[(0 + rotation) % 4]);  //L-B
//        flushIfUnBatched();
//    }

    //Slice and stitch

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, Rectangle rectangle, Borders borders, int argb) {
        dynamicTex(material, (int) rectangle.x(), (int) rectangle.y(), (int) rectangle.width(), (int) rectangle.height(), (int) borders.top(), (int) borders.left(), (int) borders.bottom(), (int) borders.right(), argb);
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, Rectangle rectangle, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int argb) {
        dynamicTex(material, (int) rectangle.x(), (int) rectangle.y(), (int) rectangle.width(), (int) rectangle.height(), topBorder, leftBorder, bottomBorder, rightBorder, argb);
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, int argb) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, r(argb), g(argb), b(argb), a(argb));
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, Rectangle rectangle, Borders borders) {
        dynamicTex(material, (int) rectangle.x(), (int) rectangle.y(), (int) rectangle.width(), (int) rectangle.height(), (int) borders.top(), (int) borders.left(), (int) borders.bottom(), (int) borders.right());
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, Rectangle rectangle, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        dynamicTex(material, (int) rectangle.x(), (int) rectangle.y(), (int) rectangle.width(), (int) rectangle.height(), topBorder, leftBorder, bottomBorder, rightBorder);
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {
        dynamicTex(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, 1F, 1F, 1F, 1F);
    }

    /**
     * This can be used to take something like a generic bordered background texture and dynamically resize it to draw at any size and shape you want.
     * This is done by cutting up the texture and stitching it back to together using, cutting and tiling as required.
     * The border parameters indicate the width of the borders around the texture, e.g. a vanilla gui texture has 4 pixel borders.
     */
    public void dynamicTex(Material material, int x, int y, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder, float red, float green, float blue, float alpha) {
//        if (batchDraw) {//Draw batched for efficiency, unless already doing a batch draw.
        dynamicTexInternal(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, red, green, blue, alpha);
//        } else {
//            batchDraw(() -> dynamicTexInternal(material, x, y, width, height, topBorder, leftBorder, bottomBorder, rightBorder, red, green, blue, alpha));
//        }
    }

    //Todo, This method can probably be made a lot more efficient.
    // Also thinking a about a data generator to automatically stitch together these textures so there is no need to generate them dynamically.
    // That would be a lot more efficient and more compatible with custom resource packs.
    private void dynamicTexInternal(Material material, int xPos, int yPos, int xSize, int ySize, int topBorder, int leftBorder, int bottomBorder, int rightBorder, float red, float green, float blue, float alpha) {
        TextureAtlasSprite sprite = material.sprite();
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
            dynamicPart(sprite, xPos + x, yPos, trimU, 0, rWidth, topBorder, red, green, blue, alpha);
            dynamicPart(sprite, xPos + x, yPos + ySize - bottomBorder, trimU, texHeight - bottomBorder, rWidth, bottomBorder, red, green, blue, alpha);

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
                    dynamicPart(sprite, xPos, yPos + y + topBorder, 0, trimV, leftBorder, rHeight, red, green, blue, alpha);
                    dynamicPart(sprite, xPos + xSize - rightBorder, yPos + y + topBorder, trimU + texWidth - rightBorder, trimV, rightBorder, rHeight, red, green, blue, alpha);
                }

                //Core
                if (y + topBorder < ySize - bottomBorder && x + leftBorder < xSize - rightBorder) {
                    dynamicPart(sprite, xPos + x + leftBorder, yPos + y + topBorder, leftBorder, topBorder, rWidth, rHeight, red, green, blue, alpha);
                }
                y += trimHeight;
            }
            x += trimWidth;
        }
    }

    private void dynamicPart(TextureAtlasSprite tex, int x, int y, float textureX, float textureY, int width, int height, float red, float green, float blue, float alpha) {
        int w = tex.contents().width();
        int h = tex.contents().height();
        wrapped.innerBlit(guiTexPipe(), tex.atlasLocation(), x, x + width, y, y + height, tex.getU((textureX / w)), tex.getU(((textureX + width) / w)), tex.getV(((textureY) / h)), tex.getV(((textureY + height) / h)), ARGB.colorFromFloat(alpha, red, green, blue));
    }

    //=== Strings ===//

    /**
     * Draw string with shadow.
     */
    public int drawString(@Nullable String message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(@Nullable String message, double x, double y, int colour, boolean shadow) {
        if (message == null) return 0;
        Component component = Component.literal(message);
        wrapped.text(font(), component, (int) x, (int) y, colour, shadow);
        return (int) x + font().width(component);
    }

    /**
     * Draw string with shadow.
     */
    public int drawString(FormattedCharSequence message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(FormattedCharSequence message, double x, double y, int colour, boolean shadow) {
        wrapped.text(font(), message, (int) x, (int) y, colour, shadow);
        return (int) x + font().width(message);
    }

    /**
     * Draw string with shadow.
     */
    public int drawString(Component message, double x, double y, int colour) {
        return drawString(message, x, y, colour, true);
    }

    public int drawString(Component message, double x, double y, int colour, boolean shadow) {
        return drawString(message.getVisualOrderText(), x, y, colour, shadow);
    }

    /**
     * Draw wrapped string with shadow.
     */
    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour) {
        drawWordWrap(message, x, y, width, colour, false);
    }

    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour, boolean shadow) {
        drawWordWrap(message, x, y, width, colour, shadow, font().lineHeight);
    }

    public void drawWordWrap(FormattedText message, double x, double y, int width, int colour, boolean shadow, double spacing) {
        for (FormattedCharSequence formattedcharsequence : font().split(message, width)) {
            drawString(formattedcharsequence, x, y, colour, shadow);
            y += spacing;
        }
    }

    /**
     * Draw centered string with shadow. (centered on x position)
     */
    public void drawCenteredString(String message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(String message, double x, double y, int colour, boolean shadow) {
        drawString(message, x - font().width(message) / 2D, y, colour, shadow);
    }

    /**
     * Draw centered string with shadow. (centered on x position)
     */
    public void drawCenteredString(Component message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(Component message, double x, double y, int colour, boolean shadow) {
        FormattedCharSequence formattedcharsequence = message.getVisualOrderText();
        drawString(formattedcharsequence, x - font().width(formattedcharsequence) / 2D, y, colour, shadow);
    }

    /**
     * Draw centered string with shadow. (centered on x position)
     */
    public void drawCenteredString(FormattedCharSequence message, double x, double y, int colour) {
        drawCenteredString(message, x, y, colour, true);
    }

    public void drawCenteredString(FormattedCharSequence message, double x, double y, int colour, boolean shadow) {
        drawString(message, x - font().width(message) / 2D, y, colour, shadow);
    }

    /**
     * If text is to long ti fit between x and xMaz, the text will scroll from left to right.
     * Otherwise, will render centered.
     * This is mostly copied from {@link AbstractWidget#renderScrollingString(GuiGraphics, Font, Component, int, int, int, int, int)}
     */
    @SuppressWarnings ("JavadocReference")
    public void drawScrollingString(Component component, double x, double y, double xMax, int colour, boolean shadow) {
        drawScrollingString(component, x, y, xMax, colour, shadow, true);
    }

    /**
     * If text is to long ti fit between x and xMaz, the text will scroll from left to right.
     * Otherwise, will render centered.
     * This is mostly copied from {@link AbstractWidget#renderScrollingString(GuiGraphics, Font, Component, int, int, int, int, int)}
     */
    @SuppressWarnings ("JavadocReference")
    public void drawScrollingString(Component component, double x, double y, double xMax, int colour, boolean shadow, boolean doScissor) {
        int textWidth = font().width(component);
        double width = xMax - x;
        if (textWidth > width) {
            double outside = textWidth - width;
            double anim = (double) Util.getMillis() / 1000.0;
            double e = Math.max(outside * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * anim / e)) / 2.0 + 0.5;
            double offset = Mth.lerp(f, 0.0, outside);
            if (doScissor) pushScissor(x, y - 1, xMax, y + font().lineHeight + 1);
            drawString(component, x - offset, y, colour, shadow);
            if (doScissor) popScissor();
        } else {
            drawCenteredString(component, (x + xMax) / 2, y, colour, shadow);
        }
    }

    //=== Tool Tips ===//

    private ItemStack tooltipStack = ItemStack.EMPTY;

    public void renderTooltip(ItemStack stack, double mouseX, double mouseY) {
        renderTooltip(stack, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
    }

    public void renderTooltip(ItemStack stack, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        this.tooltipStack = stack;
        this.toolTipWithImage(Screen.getTooltipFromItem(this.mc(), stack), stack.getTooltipImage(), mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom);
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void toolTipWithImage(List<Component> tooltips, Optional<TooltipComponent> tooltipImage, ItemStack stack, double mouseX, double mouseY) {
        toolTipWithImage(tooltips, tooltipImage, stack, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
    }

    public void toolTipWithImage(List<Component> tooltips, Optional<TooltipComponent> tooltipImage, ItemStack stack, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        this.tooltipStack = stack;
        this.toolTipWithImage(tooltips, tooltipImage, mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom);
        this.tooltipStack = ItemStack.EMPTY;
    }

    public void toolTipWithImage(List<Component> tooltip, Optional<TooltipComponent> tooltipImage, double mouseX, double mouseY) {
        toolTipWithImage(tooltip, tooltipImage, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
    }

    public void toolTipWithImage(List<Component> tooltip, Optional<TooltipComponent> tooltipImage, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        //TODO
//        List<ClientTooltipComponent> list = PolyLibClient.postGatherTooltipComponents(this.tooltipStack, tooltip, tooltipImage, (int) mouseX, guiWidth(), guiHeight(), font());
//        this.renderTooltipInternal(list, mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, DefaultTooltipPositioner.INSTANCE);
    }

    public void renderTooltip(Component message, double mouseX, double mouseY) {
        renderTooltip(message, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
    }

    public void renderTooltip(Component message, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        //TODO
//        List<ClientTooltipComponent> list = PolyLibClient.postGatherTooltipComponents(this.tooltipStack, List.of(message), Optional.empty(), (int) mouseX, guiWidth(), guiHeight(), font());
//        this.renderTooltipInternal(list, mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, DefaultTooltipPositioner.INSTANCE);
    }

    public void componentTooltip(List<Component> tooltips, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        //TODO
//        List<ClientTooltipComponent> components = PolyLibClient.postGatherTooltipComponents(this.tooltipStack, tooltips, Optional.empty(), (int) mouseX, guiWidth(), guiHeight(), font());
//        this.renderTooltipInternal(components, mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, DefaultTooltipPositioner.INSTANCE);
    }

    public void componentTooltip(List<? extends FormattedText> tooltips, double mouseX, double mouseY, ItemStack stack) {
        componentTooltip(tooltips, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f, stack);
    }

    public void componentTooltip(List<? extends FormattedText> tooltips, double mouseX, double mouseY) {
        componentTooltip(tooltips, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f, ItemStack.EMPTY);
    }

    public void componentTooltip(List<? extends FormattedText> tooltips, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, ItemStack stack) {
        this.tooltipStack = stack;
        //TODO
//        List<ClientTooltipComponent> components = PolyLibClient.postGatherTooltipComponents(stack, tooltips, Optional.empty(), (int) mouseX, guiWidth(), guiHeight(), font());
//        this.renderTooltipInternal(components, mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, DefaultTooltipPositioner.INSTANCE);
        this.tooltipStack = ItemStack.EMPTY;
    }

    /**
     * Warning: This tooltip method with not automatically wrap tooltip lines
     */
    public void renderTooltip(List<? extends FormattedCharSequence> tooltips, double mouseX, double mouseY) {
        renderTooltip(tooltips, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
    }

    /**
     * Warning: This tooltip method with not automatically wrap tooltip lines
     */
    public void renderTooltip(List<? extends FormattedCharSequence> tooltips, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        this.renderTooltipInternal(tooltips.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, DefaultTooltipPositioner.INSTANCE);
    }

    /**
     * Warning: This tooltip method with not automatically wrap tooltip lines
     */
    public void renderTooltip(List<FormattedCharSequence> tooltips, ClientTooltipPositioner positioner, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
        this.renderTooltipInternal(tooltips.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mouseX, mouseY, backgroundTop, backgroundBottom, borderTop, borderBottom, positioner);
    }

    private void renderTooltipInternal(List<ClientTooltipComponent> tooltips, double mouseX, double mouseY, int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, ClientTooltipPositioner positioner) {
        //TODO
//        if (!tooltips.isEmpty()) {
//            PolyLibClient.ToolTipResult event = PolyLibClient.postRenderTooltipPre(this.tooltipStack, wrapped, (int) mouseX, (int) mouseY, guiWidth(), guiHeight(), tooltips, font(), positioner);
//            if (event.canceled()) return;
//
//            int width = 0;
//            int height = tooltips.size() == 1 ? -2 : 0;
//            for (ClientTooltipComponent line : tooltips) {
//                width = Math.max(width, line.getWidth(event.getFont()));
//                height += line.getHeight(font());
//            }
//
//            Vector2ic position = positioner.positionTooltip(guiWidth(), guiHeight(), event.getX(), event.getY(), width, height);
//            int xPos = position.x();
//            int yPos = Math.max(position.y(), 3); //Default positioner allows negative y-pos for some reason...
//
//            wrapped.pose().pushMatrix();
//            PolyLibClient.ToolTipColour colour = PolyLibClient.postTooltipColour(tooltipStack, wrapped, xPos, yPos, backgroundTop, backgroundBottom, borderTop, borderBottom, event.getFont(), tooltips);
//            toolTipBackground(xPos - 3, yPos - 3, width + 6, height + 6, colour.getBackgroundStart(), colour.getBackgroundEnd(), colour.getBorderStart(), colour.getBorderEnd(), true);
//            int linePos = yPos;
//
//            for (int i = 0; i < tooltips.size(); ++i) {
//                ClientTooltipComponent component = tooltips.get(i);
//                component.extractText(wrapped, event.getFont(), xPos, linePos);
//                linePos += component.getHeight(font()) + (i == 0 ? 2 : 0);
//            }
//
//            linePos = yPos;
//
//            for (int i = 0; i < tooltips.size(); ++i) {
//                ClientTooltipComponent component = tooltips.get(i);
//                component.extractImage(event.getFont(), xPos, linePos, width, height, wrapped);
//                linePos += component.getHeight(font()) + (i == 0 ? 2 : 0);
//            }
//            wrapped.pose().popMatrix();
//        }
    }

    public void renderComponentHoverEffect(@Nullable Style style, int mouseX, int mouseY) {
        if (style == null || style.getHoverEvent() == null) {
            return;
        }
        HoverEvent event = style.getHoverEvent();
        if (event instanceof HoverEvent.ShowItem(ItemStackTemplate item)) {
            this.renderTooltip(item.create(), mouseX, mouseY);
        } else if (event instanceof HoverEvent.ShowEntity(HoverEvent.EntityTooltipInfo entity)) {
            this.componentTooltip(entity.getTooltipLines(), mouseX, mouseY);
        } else if (event instanceof HoverEvent.ShowText(Component value)) {
            this.renderTooltip(font().split(value, Math.max(this.guiWidth() / 2, 200)), mouseX, mouseY);
        }
    }

    //=== ItemStacks ===//

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is 32, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderItem(ItemStack stack, double x, double y) {
        renderItem(stack, x, y, 16);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is size*2, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderItem(ItemStack stack, double x, double y, double size) {
        this.renderItem(mc().player, mc().level, stack, x, y, size, 0);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is size*2, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderItem(ItemStack stack, double x, double y, double size, int modelRand) {
        this.renderItem(mc().player, mc().level, stack, x, y, size, modelRand);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is 32, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderFakeItem(ItemStack stack, double x, double y) {
        renderFakeItem(stack, x, y, 16);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is size*2, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderFakeItem(ItemStack stack, double x, double y, double size) {
        this.renderItem(null, mc().level, stack, x, y, size, 0);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is 32, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderItem(LivingEntity entity, ItemStack stack, double x, double y, int modelRand) {
        renderItem(entity, stack, x, y, 16, modelRand);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is size*2, This must be accounted for when setting the z depth in an element using this render method.
     */
    public void renderItem(LivingEntity entity, ItemStack stack, double x, double y, double size, int modelRand) {
        this.renderItem(entity, entity.level(), stack, x, y, size, modelRand);
    }

    /**
     * Renders an item stack on the screen.
     * Important Note: Required z clearance is size*2, This must be accounted for when setting the z depth in an element using this render method.
     *
     * @param size      Width and height of the stack in pixels (Standard default is 16)
     * @param modelRand A somewhat random value used in model gathering, Not very important, Can just use 0 or x/y position.
     */
    public void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, double x, double y, double size, int modelRand) {
        if (stack.isEmpty()) return;

        wrapped.pose().pushMatrix();
        wrapped.pose().translate((float) x, (float) y);
        wrapped.pose().scale((float) size / 16F, (float) size / 16F);
        wrapped.item(entity, stack, (int) 0, (int) 0, modelRand);
        wrapped.pose().popMatrix();
    }

    /**
     * Draw item decorations (Count, Damage, Cool-down)
     * This should be rendered at the same position and size as the item.
     * There is no need to fiddle with z offsets or anything, just call renderItemDecorations after renderItem and it will work.
     * Z depth requirements are the same as the renderItem method.
     */
    public void renderItemDecorations(ItemStack stack, double x, double y) {
        renderItemDecorations(stack, x, y, 16);
    }

    /**
     * Draw item decorations (Count, Damage, Cool-down)
     * This should be rendered at the same position and size as the item.
     * There is no need to fiddle with z offsets or anything, just call renderItemDecorations after renderItem and it will work.
     * Z depth requirements are the same as the renderItem method.
     */
    public void renderItemDecorations(ItemStack stack, double x, double y, double size) {
        this.renderItemDecorations(stack, x, y, size, null);
    }

    /**
     * Draw item decorations (Count, Damage, Cool-down)
     * This should be rendered at the same position and size as the item.
     * There is no need to fiddle with z offsets or anything, just call renderItemDecorations after renderItem and it will work.
     * Z depth requirements are the same as the renderItem method.
     */
    public void renderItemDecorations(ItemStack stack, double x, double y, @Nullable String text) {
        renderItemDecorations(stack, x, y, 16, text);
    }

    /**
     * Draw item decorations (Count, Damage, Cool-down)
     * This should be rendered at the same position and size as the item.
     * There is no need to fiddle with z offsets or anything, just call renderItemDecorations after renderItem and it will work.
     * Z depth requirements are the same as the renderItem method.
     */
    public void renderItemDecorations(ItemStack stack, double x, double y, double size, @Nullable String text) {
        if (stack.isEmpty()) {
            return;
        }

        wrapped.pose().pushMatrix();
        wrapped.pose().translate((float) x, (float) y);
        wrapped.pose().scale((float) size / 16F, (float) size / 16F);
        wrapped.itemDecorations(font(), stack, 0, 0, text);
        wrapped.pose().popMatrix();
    }


    //=== Entity? ===//


    //=== Render Utils ===//

    public void pushScissorRect(Rectangle rectangle) {
        pushScissorRect(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
    }

    public void pushScissorRect(double x, double y, double width, double height) {
//        flushIfBatched();
//        scissorHandler.pushGuiScissor(x, y, width, height);
        wrapped.enableScissor((int) x, (int) y, (int) (x + width), (int) (y + height));
    }

    public void pushScissor(double xMin, double yMin, double xMax, double yMax) {
//        flushIfBatched();
//        scissorHandler.pushGuiScissor(xMin, yMin, xMax - xMin, yMax - yMin);
        wrapped.enableScissor((int) xMin, (int) yMin, (int) (xMax), (int) (yMax));
    }

    public void popScissor() {
        wrapped.disableScissor();
    }

//    /**
//     * Sets the render system shader colour, Effect will vary depending on what is being rendered.
//     * Ideally this should be avoided in favor of render calls that accept colour.
//     */
//    public void setColor(float red, float green, float blue, float alpha) {
//        this.flushIfBatched();
//        RenderSystem.setShaderColor(red, green, blue, alpha);
//    }

    public Matrix3x2f translate(double x, double y) {
        return wrapped.pose().translate((float) x, (float) y);
    }

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
        return ARGB.red(argb) / 255F;
    }

    private static float g(int argb) {
        return ARGB.green(argb) / 255F;

    }

    private static float b(int argb) {
        return ARGB.blue(argb) / 255F;

    }

    private static float a(int argb) {
        return ARGB.alpha(argb) / 255F;

    }

    //Render Type Builders

    public static RenderPipeline guiSolidPipe() {
        return RenderPipelines.GUI;
    }

    public static RenderPipeline guiTexPipe() {
        return RenderPipelines.GUI_TEXTURED;
    }

    //TODO
//    public static RenderType texColType(Identifier location) {
//        return GUI_TEXTURED.apply(location);
//    }

    //TODO
    //I'm not ready to deal with foguring out rener pipelines yet, so for now... Yoink!
//    private static final Function<Identifier, RenderType> GUI_TEXTURED = Util.memoize((arg) ->
//            create("gui_textured", 786432, RenderPipelines.GUI_TEXTURED, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(arg, false)).createCompositeState(false)));
//
//    private static RenderType.CompositeRenderType create(String string, int i, RenderPipeline renderPipeline, RenderType.CompositeState arg) {
//        return create(string, i, false, false, renderPipeline, arg);
//    }
//
//    private static RenderType.CompositeRenderType create(String string, int i, boolean bl, boolean bl2, RenderPipeline renderPipeline, RenderType.CompositeState arg) {
//        return new RenderType.CompositeRenderType(string, i, bl, bl2, renderPipeline, arg);
//    }
}
