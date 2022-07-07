package net.creeperhost.polylib.client.render.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;

public class FluidRenderMap<V> extends Object2ObjectOpenCustomHashMap<FluidStack, V>
{
    public enum FluidType
    {
        STILL, FLOWING
    }

    public FluidRenderMap()
    {
        super(FluidHashStrategy.INSTANCE);
    }

    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack, FluidType type)
    {
        Fluid fluid = fluidStack.getFluid();
        if (type == FluidType.STILL)
        {
            return FluidStackHooks.getStillTexture(fluid);
        }
        else
        {
            return FluidStackHooks.getFlowingTexture(fluid);
        }
    }

    public static class FluidHashStrategy implements Hash.Strategy<FluidStack>
    {

        public static FluidHashStrategy INSTANCE = new FluidHashStrategy();

        @Override
        public int hashCode(FluidStack stack)
        {
            if (stack == null || stack.isEmpty())
            {
                return 0;
            }
            int code = 1;
            code = 31 * code + stack.getFluid().hashCode();
            if (stack.hasTag())
            {
                code = 31 * code + stack.getTag().hashCode();
            }
            return code;
        }

        @Override
        public boolean equals(FluidStack a, FluidStack b)
        {
            return a == null ? b == null : b != null && a.isFluidEqual(b);
        }
    }
}
