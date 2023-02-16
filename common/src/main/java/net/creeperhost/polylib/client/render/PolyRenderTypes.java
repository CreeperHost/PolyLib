package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

public class PolyRenderTypes
{
    public static final RenderType RESIZABLE = RenderType.create(PolyLib.MOD_ID + ":resizable_cuboid", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS,
            256, true, false, RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_CUTOUT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                    .setCullState(RenderType.CULL)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .setWriteMaskState(RenderType.COLOR_WRITE)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .createCompositeState(true));
}
