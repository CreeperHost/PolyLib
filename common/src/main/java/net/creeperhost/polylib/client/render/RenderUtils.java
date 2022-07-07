package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.creeperhost.polylib.client.model.Model3D;

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
}
