package net.creeperhost.polylib.client.modulargui.nodegraph.render;

import net.creeperhost.polylib.client.modulargui.elements.GuiNodeCanvas;
import org.joml.Matrix3x2f;

/**
 * Utility that provides the GPU bezier {@link GuiNodeCanvas.BezierRenderer} implementation
 * for NeoForge.
 *
 * <p>Call {@link #renderer()} at canvas construction time to wire GPU bezier rendering:
 * <pre>
 *     GuiNodeCanvas canvas = new GuiNodeCanvas(parent);
 *     canvas.setBezierRenderer(NodeCanvasBezierRenderer.renderer());
 * </pre>
 */
public final class NodeCanvasBezierRenderer {

    private NodeCanvasBezierRenderer() {}

    /**
     * Returns a {@link GuiNodeCanvas.BezierRenderer} that submits a GPU distance-field
     * bezier wire via the NeoForge PiP system.
     */
    public static GuiNodeCanvas.BezierRenderer renderer() {
        return (render, pose, screenPts, colour) ->
                render.graphics().submitPictureInPictureRenderState(
                        BezierWireRenderState.fromScreen(new Matrix3x2f(pose), screenPts, colour));
    }
}
