package net.creeperhost.polylib.client.render.rendertypes;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.creeperhost.polylib.client.render.PolyRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class FluidTankRenderType extends RenderType
{
//    private FluidTankRenderType(String nameIn, VertexFormat formatIn, VertexFormat.Mode drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn)
//    {
//        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
//    }

    @Deprecated
    public static final RenderType RESIZABLE = PolyRenderTypes.RESIZABLE;

    public FluidTankRenderType(String string, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, i, bl, bl2, runnable, runnable2);
    }

    @Override
    public void draw(MeshData meshData) {

    }

    @Override
    public RenderTarget getRenderTarget() {
        return null;
    }

    @Override
    public RenderPipeline getRenderPipeline() {
        return null;
    }

    @Override
    public VertexFormat format() {
        return null;
    }

    @Override
    public VertexFormat.Mode mode() {
        return null;
    }
}
