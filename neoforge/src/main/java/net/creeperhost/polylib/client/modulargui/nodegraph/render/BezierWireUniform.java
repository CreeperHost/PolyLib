package net.creeperhost.polylib.client.modulargui.nodegraph.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.joml.Vector2fc;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * GPU uniform block for the PolyLib bezier wire shader.
 *
 * <p>std140 layout:
 * <pre>
 *   vec2 p0   – normalised control point 0 (start)
 *   vec2 p1   – normalised control point 1 (out-tangent)
 *   vec2 p2   – normalised control point 2 (in-tangent)
 *   vec2 p3   – normalised control point 3 (end)
 *   vec2 size – PiP texture pixel dimensions
 * </pre>
 *
 * <p>Call {@code STORAGE.get().endFrame()} in a {@code RenderFrameEvent.Post}
 * handler to release UBO slots at the end of each frame.
 */
public record BezierWireUniform(Vector2fc[] controlPoints, ScreenRectangle area)
        implements DynamicUniformStorage.DynamicUniform {

    public static final String NAME = "BezierWire";

    public static final Supplier<DynamicUniformStorage<BezierWireUniform>> STORAGE =
            Suppliers.memoize(() -> new DynamicUniformStorage<>(
                    NAME + " UBO",
                    new Std140SizeCalculator()
                            .putVec2().putVec2().putVec2().putVec2() // 4 control points
                            .putVec2()                               // size
                            .get(),
                    64
            ));

    @Override
    public void write(ByteBuffer buffer) {
        Std140Builder.intoBuffer(buffer)
                .putVec2(controlPoints[0])
                .putVec2(controlPoints[1])
                .putVec2(controlPoints[2])
                .putVec2(controlPoints[3])
                .putVec2(area.width(), area.height())
                .get();
    }
}
