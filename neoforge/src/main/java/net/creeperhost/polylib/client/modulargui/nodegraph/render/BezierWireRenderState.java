package net.creeperhost.polylib.client.modulargui.nodegraph.render;

import net.creeperhost.polylib.client.modulargui.elements.GuiNodeCanvas;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.jspecify.annotations.Nullable;

/**
 * Immutable render-state for one GPU bezier wire.
 *
 * <p>Submit via {@code GuiGraphicsExtractor.submitPictureInPictureRenderState(state)},
 * accessible through PolyLib's {@code GuiRender.graphics()}.
 *
 * <p>Use as the {@link GuiNodeCanvas.BezierStateFactory}:
 * <pre>
 *     canvas.setBezierStateFactory(BezierWireRenderState::fromScreen);
 * </pre>
 */
public record BezierWireRenderState(
        Matrix3x2f pose,
        Vector2fc[] controlPoints,   // normalised [0,1] within bounds
        int colour,
        ScreenRectangle bounds
) implements PictureInPictureRenderState {

    /**
     * Build from four screen-space control points and the current GUI pose.
     *
     * @param pose       2D GUI pose (copy of {@code GuiRender.pose()})
     * @param screenPts  4 control points in GUI pixel space
     * @param colour     ARGB wire colour
     */
    public static BezierWireRenderState fromScreen(Matrix3x2f pose,
                                                    Vector2fc[] screenPts,
                                                    int colour) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Vector2fc p : screenPts) {
            float tx = pose.m00() * p.x() + pose.m10() * p.y() + pose.m20();
            float ty = pose.m01() * p.x() + pose.m11() * p.y() + pose.m21();
            if (tx < minX) minX = tx;
            if (ty < minY) minY = ty;
            if (tx > maxX) maxX = tx;
            if (ty > maxY) maxY = ty;
        }
        int bx = (int) Math.floor(minX);
        int by = (int) Math.floor(minY);
        int bw = Math.max(1, (int) Math.ceil(maxX) - bx);
        int bh = Math.max(1, (int) Math.ceil(maxY) - by);
        ScreenRectangle bounds = new ScreenRectangle(bx, by, bw, bh);

        Vector2fc[] rel = new Vector2fc[4];
        for (int i = 0; i < 4; i++) {
            float tx = pose.m00() * screenPts[i].x() + pose.m10() * screenPts[i].y() + pose.m20();
            float ty = pose.m01() * screenPts[i].x() + pose.m11() * screenPts[i].y() + pose.m21();
            rel[i] = new Vector2f((tx - bx) / (float) bw, (ty - by) / (float) bh);
        }
        return new BezierWireRenderState(pose, rel, colour, bounds);
    }

    @Override public int x0()    { return bounds.left();   }
    @Override public int x1()    { return bounds.right();  }
    @Override public int y0()    { return bounds.top();    }
    @Override public int y1()    { return bounds.bottom(); }
    @Override public float scale() { return 1f; }
    @Override public @Nullable ScreenRectangle scissorArea() { return null; }
}
