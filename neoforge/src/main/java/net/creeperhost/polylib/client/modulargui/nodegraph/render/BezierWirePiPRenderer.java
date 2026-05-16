package net.creeperhost.polylib.client.modulargui.nodegraph.render;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * NeoForge PiP renderer that draws a single GPU bezier wire per submitted state.
 *
 * <p>Register once via {@code RegisterPictureInPictureRenderersEvent}:
 * <pre>
 *     event.register(BezierWireRenderState.class, BezierWirePiPRenderer::new);
 * </pre>
 *
 * <p>Call {@code BezierWireUniform.STORAGE.get().endFrame()} in
 * {@code RenderFrameEvent.Post} to reclaim UBO ring-buffer slots each frame.
 *
 * <p>Textures are cached: if the render state is unchanged from the last frame
 * the GPU texture is reused without re-rendering.
 */
public class BezierWirePiPRenderer
        extends net.minecraft.client.gui.render.pip.PictureInPictureRenderer<BezierWireRenderState> {

    private BezierWireRenderState lastState;

    public BezierWirePiPRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public @NotNull Class<BezierWireRenderState> getRenderStateClass() {
        return BezierWireRenderState.class;
    }

    @Override
    protected boolean textureIsReadyToBlit(BezierWireRenderState state) {
        return this.lastState != null && this.lastState.equals(state);
    }

    @Override
    protected void renderToTexture(BezierWireRenderState state, @NonNull PoseStack poseStack) {
        ScreenRectangle bounds = state.bounds();
        float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        float sw = bounds.width()  * scale;
        float sh = bounds.height() * scale;
        ScreenRectangle scaledBounds = new ScreenRectangle(0, 0, (int) sw, (int) sh);
        BezierWireUniform uniform = new BezierWireUniform(state.controlPoints(), scaledBounds);
        GpuDevice device = RenderSystem.getDevice();
        var target = Minecraft.getInstance().getMainRenderTarget();
        try (var byteBuffer = new ByteBufferBuilder(256)) {
            var buffer = new BufferBuilder(byteBuffer, VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.POSITION_COLOR);
            buffer.addVertex(0f,  0f,  0f).setColor(state.colour());
            buffer.addVertex(0f,  sh,  0f).setColor(state.colour());
            buffer.addVertex(sw,  sh,  0f).setColor(state.colour());
            buffer.addVertex(sw,  0f,  0f).setColor(state.colour());
            MeshData mesh = buffer.buildOrThrow();
            var gpuBufs = buildGpuBuffers(mesh);
            var encoder = device.createCommandEncoder();
            var dynamicUniforms = RenderSystem.getDynamicUniforms().writeTransform(
                    RenderSystem.getModelViewMatrix(),
                    new org.joml.Vector4f(1f, 1f, 1f, 1f),
                    new org.joml.Vector3f(),
                    new org.joml.Matrix4f()
            );
            var bezierSlice = BezierWireUniform.STORAGE.get().writeUniform(uniform);
            try (mesh; var pass = encoder.createRenderPass(
                    () -> "PolyLib BezierWire PiP",
                    Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, target.getColorTextureView()),
                    OptionalInt.empty()
            )) {
                pass.setPipeline(NodeCanvasRenderPipelines.BEZIER_WIRE);
                RenderSystem.bindDefaultUniforms(pass);
                pass.setUniform("DynamicTransforms", dynamicUniforms);
                pass.setUniform(BezierWireUniform.NAME, bezierSlice);
                pass.setVertexBuffer(0, gpuBufs.vertex());
                pass.setIndexBuffer(gpuBufs.index(), gpuBufs.type());
                pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
            }
        }
        this.lastState = state;
    }

    @Override
    protected @NotNull String getTextureLabel() {
        return "polylib_bezier_wire";
    }

    private record GpuBufs(
            com.mojang.blaze3d.buffers.GpuBuffer vertex,
            com.mojang.blaze3d.buffers.GpuBuffer index,
            VertexFormat.IndexType type) {}

    private static GpuBufs buildGpuBuffers(MeshData mesh) {
        var pipeline = NodeCanvasRenderPipelines.BEZIER_WIRE;
        com.mojang.blaze3d.buffers.GpuBuffer vertex =
                pipeline.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());
        if (mesh.indexBuffer() == null) {
            var seq = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
            return new GpuBufs(vertex, seq.getBuffer(mesh.drawState().indexCount()), seq.type());
        }
        return new GpuBufs(
                vertex,
                pipeline.getVertexFormat().uploadImmediateIndexBuffer(mesh.indexBuffer()),
                mesh.drawState().indexType()
        );
    }
}
