package net.creeperhost.polylib.client.render.rendertypes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public class GhostRenderType extends RenderType
{
    private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

    private GhostRenderType(RenderType original)
    {
        super(String.format("%s_%s_ghost", original.toString(), PolyLib.MOD_ID), original.format(), original.mode(),
                original.bufferSize(), original.affectsCrumbling(), true, () ->
                {
                    original.setupRenderState();

                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(1, 1, 1, 0.4F);
                }, () ->
                {
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                    RenderSystem.enableDepthTest();

                    original.clearRenderState();
                });
    }

    public static RenderType remap(RenderType in)
    {
        if (in instanceof GhostRenderType) return in;
        return remappedTypes.computeIfAbsent(in, GhostRenderType::new);
    }

    public static class GhostBuffers extends MultiBufferSource.BufferSource
    {
        public GhostBuffers(BufferBuilder fallback, Map<RenderType, BufferBuilder> layerBuffers)
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
