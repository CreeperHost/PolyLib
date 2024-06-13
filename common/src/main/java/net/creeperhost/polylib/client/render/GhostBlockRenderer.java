package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.creeperhost.polylib.client.render.rendertypes.GhostRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.SequencedMap;

public class GhostBlockRenderer
{
    public static void renderBlock(BlockState state, BlockPos pos, PoseStack ms, MultiBufferSource buffers)
    {
        if (state == null) return;
        if (pos != null)
        {
            ms.pushPose();
            ms.translate(pos.getX(), pos.getY(), pos.getZ());

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, ms, buffers, 0xF000F0,
                    OverlayTexture.NO_OVERLAY);
            ms.popPose();
        }
    }

    public static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original)
    {
        ByteBufferBuilder fallback = original.sharedBuffer;
        SequencedMap<RenderType, ByteBufferBuilder> layerBuffers = original.fixedBuffers;

        SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();

        for (Map.Entry<RenderType, ByteBufferBuilder> e : layerBuffers.entrySet())
        {
            remapped.put(GhostRenderType.remap(e.getKey()), e.getValue());
        }
        return new GhostRenderType.GhostBuffers(fallback, remapped);
    }
}
