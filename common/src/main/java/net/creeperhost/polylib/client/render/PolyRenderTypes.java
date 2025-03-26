package net.creeperhost.polylib.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.inventory.InventoryMenu;

public class PolyRenderTypes
{
    private static final ResourceLocation BLOCK_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");

    //TODO 1.21.5
//    public static final RenderType RESIZABLE = RenderType.create(PolyLib.MOD_ID + ":resizable_cuboid",
//            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, false,
//            RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_CUTOUT_SHADER).setTextureState(
//                    new RenderStateShard.TextureStateShard(BLOCK_ATLAS, TriState.FALSE, false)).setCullState(
//                    RenderType.CULL).setLightmapState(RenderType.LIGHTMAP).setWriteMaskState(
//                    RenderType.COLOR_WRITE).setLightmapState(RenderType.LIGHTMAP).createCompositeState(true));

    //TODO this is only here to cut down on errors
    public static final RenderType RESIZABLE = RenderType.gui();
}
