package net.creeperhost.polylib.client.modulargui.nodegraph.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;

/**
 * NeoForge render pipelines for the PolyLib node canvas.
 *
 * <p>Register from a {@code RegisterRenderPipelinesEvent} listener:
 * <pre>
 *     event.registerPipeline(NodeCanvasRenderPipelines.BEZIER_WIRE);
 * </pre>
 *
 * <p>Shaders are at {@code assets/polylib/shaders/bezier_wire.vsh/.fsh}
 * in the common module resources.
 */
public final class NodeCanvasRenderPipelines {

    public static final RenderPipeline BEZIER_WIRE = RenderPipeline.builder()
            .withUniform("DynamicTransforms",    UniformType.UNIFORM_BUFFER)
            .withUniform("Projection",           UniformType.UNIFORM_BUFFER)
            .withUniform(BezierWireUniform.NAME, UniformType.UNIFORM_BUFFER)
            .withLocation(Identifier.fromNamespaceAndPath("polylib", "pipeline/bezier_wire"))
            .withVertexShader(Identifier.fromNamespaceAndPath("polylib", "bezier_wire"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("polylib", "bezier_wire"))
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .build();

    private NodeCanvasRenderPipelines() {}
}
