package net.creeperhost.polylib.client.modulargui.lib;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 11/5/20.
 */
public class CursorHelper {

    public static final ResourceLocation DRAG = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/cursors/drag.png");
    public static final ResourceLocation RESIZE_H = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/cursors/resize_h.png");
    public static final ResourceLocation RESIZE_V = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/cursors/resize_v.png");
    public static final ResourceLocation RESIZE_TRBL = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/cursors/resize_diag_trbl.png");
    public static final ResourceLocation RESIZE_TLBR = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/cursors/resize_diag_tlbr.png");
    private static Map<ResourceLocation, Long> cursors = new HashMap<>();
    private static ResourceLocation active = null;

    public static void init() {
        ClientGuiEvent.SET_SCREEN.register(screen -> {
            resetCursor();
            return CompoundEventResult.pass();
        });
    }

    private static long createCursor(ResourceLocation resource) {
        try {
            BufferedImage bufferedimage = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(resource).get().open());
            GLFWImage glfwImage = imageToGLFWImage(bufferedimage);
            return GLFW.glfwCreateCursor(glfwImage, 16, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static GLFWImage imageToGLFWImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
            final BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            final Graphics2D graphics = convertedImage.createGraphics();
            final int targetWidth = image.getWidth();
            final int targetHeight = image.getHeight();
            graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
            graphics.dispose();
            image = convertedImage;
        }
        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int colorSpace = image.getRGB(j, i);
                buffer.put((byte) ((colorSpace << 8) >> 24));
                buffer.put((byte) ((colorSpace << 16) >> 24));
                buffer.put((byte) ((colorSpace << 24) >> 24));
                buffer.put((byte) (colorSpace >> 24));
            }
        }
        buffer.flip();
        final GLFWImage result = GLFWImage.create();
        result.set(image.getWidth(), image.getHeight(), buffer);
        return result;
    }

    public static void setCursor(@Nullable ResourceLocation cursor) {
        if (cursor != active) {
            active = cursor;
            long window = Minecraft.getInstance().getWindow().getWindow();
            long newCursor = active == null ? 0 : cursors.computeIfAbsent(cursor, CursorHelper::createCursor);
            try {
                GLFW.glfwSetCursor(window, newCursor);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetCursor() {
        if (active != null) {
            setCursor(null);
        }
    }
}
