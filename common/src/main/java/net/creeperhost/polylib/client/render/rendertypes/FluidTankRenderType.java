package net.creeperhost.polylib.client.render.rendertypes;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.creeperhost.polylib.client.render.PolyRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class FluidTankRenderType extends RenderType
{
    private FluidTankRenderType(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
    {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    @Deprecated
    public static final RenderType RESIZABLE = PolyRenderTypes.RESIZABLE;
}
