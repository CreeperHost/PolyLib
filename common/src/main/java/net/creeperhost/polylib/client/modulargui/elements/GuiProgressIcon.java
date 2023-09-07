package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Axis;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Direction;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * This can be used to create a simple progress indicator like those used in machines like furnaces.
 * <p>
 * The background texture (if one is used) and the animated texture must be the same shape and size,
 * They must be designed so that the animated texture can be rendered directly on top of the background texture with no offset.
 * The animated texture should not have any empty space on ether end as the entire width of the texture is used in the animation.
 * <p>
 * Texture must be designed for left to right animation,
 * <p>
 * Created by brandon3055 on 04/09/2023
 */
public class GuiProgressIcon extends GuiElement<GuiProgressIcon> implements BackgroundRender {

    private Material background = null;
    private Material animated;
    private Supplier<Double> progress = () -> 0D;
    private Direction direction = Direction.RIGHT;

    public GuiProgressIcon(@NotNull GuiParent<?> parent, Material animated) {
        super(parent);
        this.animated = animated;
    }

    public GuiProgressIcon(@NotNull GuiParent<?> parent, Material background, Material animated) {
        super(parent);
        this.background = background;
        this.animated = animated;
    }

    public GuiProgressIcon(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    /**
     * Set the direction this progress icon is pointing, Default is RIGHT
     */
    public GuiProgressIcon setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Sets the background texture, aka the "empty" texture.
     */
    public GuiProgressIcon setBackground(@Nullable Material background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the texture that will be animated.
     */
    public GuiProgressIcon setAnimated(Material animated) {
        this.animated = animated;
        return this;
    }

    /**
     * Set the current progress to a fixed value.
     *
     * @see #setProgress(Supplier)
     */
    public GuiProgressIcon setProgress(double progress) {
        return setProgress(() -> progress);
    }

    /**
     * Attach a supplier that returns the current progress value for this progress icon (0 to 1)
     */
    public GuiProgressIcon setProgress(Supplier<Double> progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        render.pose().pushPose();

        double width = direction.getAxis() == Axis.X ? xSize() : ySize();
        double height = direction.getAxis() == Axis.X ? ySize() : xSize();

        render.pose().translate(xMin() + (xSize() / 2), yMin() + (ySize() / 2), 0);
        render.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) Direction.RIGHT.rotationTo(direction)));

        double halfWidth = width / 2;
        double halfHeight = height / 2;
        if (background != null) {
            render.tex(background, -halfWidth, -halfHeight, halfWidth, halfHeight, 0xFFFFFFFF);
        }

        if (animated == null) return;
        float progress = this.progress.get().floatValue();
        render.partialSprite(animated.renderType(GuiRender::texColType), -halfWidth, -halfHeight, -halfWidth + (width* progress), -halfHeight + height, animated.sprite(), 0F, 0F, progress, 1F, 0xFFFFFFFF);

        render.pose().popPose();
    }
}