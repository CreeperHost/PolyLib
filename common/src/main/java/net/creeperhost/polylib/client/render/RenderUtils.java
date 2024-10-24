package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.creeperhost.polylib.client.model.Model3D;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

@Deprecated //I dont think any of this isi needed anymore
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
        bufferBuilder.addVertex(matrix4f, (float) k, (float) j, (float) m).setColor(g, h, p, f);
        bufferBuilder.addVertex(matrix4f, (float) i, (float) j, (float) m).setColor(g, h, p, f);
        bufferBuilder.addVertex(matrix4f, (float) i, (float) l, (float) m).setColor(r, s, t, q);
        bufferBuilder.addVertex(matrix4f, (float) k, (float) l, (float) m).setColor(r, s, t, q);
    }
}
