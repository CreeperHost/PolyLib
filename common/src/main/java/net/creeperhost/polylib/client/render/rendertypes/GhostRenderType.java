package net.creeperhost.polylib.client.render.rendertypes;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.SequencedMap;

public class GhostRenderType extends RenderType
{
    private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

    public GhostRenderType(String string, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, i, bl, bl2, runnable, runnable2);
    }

    //TODO 1.21.5
//    private GhostRenderType(RenderType original)
//    {
//        super(String.format("%s_%s_ghost", original.toString(), PolyLib.MOD_ID), original.format(), original.mode(),
//                original.bufferSize(), original.affectsCrumbling(), true, () ->
//                {
//                    original.setupRenderState();
//
//                    RenderSystem.disableDepthTest();
//                    RenderSystem.enableBlend();
//                    RenderSystem.setShaderColor(1, 1, 1, 0.4F);
//                }, () ->
//                {
//                    RenderSystem.setShaderColor(1, 1, 1, 1);
//                    RenderSystem.disableBlend();
//                    RenderSystem.enableDepthTest();
//
//                    original.clearRenderState();
//                });
//    }

    public static RenderType remap(RenderType in)
    {
        if (in instanceof GhostRenderType) return in;
        //TODO 1.21.5
//        return remappedTypes.computeIfAbsent(in, GhostRenderType::new);
        return remappedTypes.get(in);
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

    public static class GhostBuffers extends MultiBufferSource.BufferSource
    {
        public GhostBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers)
        {
            super(fallback, layerBuffers);
        }

        @Override
        public @NotNull VertexConsumer getBuffer(@NotNull RenderType type)
        {
            return super.getBuffer(GhostRenderType.remap(type));
        }
    }
}
