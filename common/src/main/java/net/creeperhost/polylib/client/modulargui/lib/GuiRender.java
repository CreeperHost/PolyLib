package net.creeperhost.polylib.client.modulargui.lib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

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

    private final Minecraft mc;
    private final PoseStack poseStack;
    private final MultiBufferSource.BufferSource buffers;

    public GuiRender(Minecraft mc, PoseStack poseStack, MultiBufferSource.BufferSource buffers) {
        this.mc = mc;
        this.poseStack = poseStack;
        this.buffers = buffers;
    }

    public GuiRender(Minecraft mc, MultiBufferSource.BufferSource buffers) {
        this(mc, new PoseStack(), buffers);
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public MultiBufferSource.BufferSource buffers() {
        return buffers;
    }

    public Minecraft mc() {
        return mc;
    }

    //=== Static Utils ===//

    public static boolean isInRect(double minX, double minY, double width, double height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    public static boolean isInRect(int minX, int minY, int width, int height, double testX, double testY) {
        return ((testX >= minX && testX < minX + width) && (testY >= minY && testY < minY + height));
    }

    //=== Render Functions ===//
}
