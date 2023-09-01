package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Used to draw a simple rectangle on the screen.
 * Can specify separate (or no) border colours and fill colours.
 * Can also render using the "shadedRectangle" render type.
 * <p>
 * Created by brandon3055 on 28/08/2023
 */
public class GuiRectangle extends GuiElement<GuiRectangle> implements BackgroundRender {
    private Supplier<Integer> fill = null;
    private Supplier<Integer> border = null;

    private Supplier<Integer> borderWidth = () -> 1;

    private Supplier<Integer> shadeTopLeft;
    private Supplier<Integer> shadeBottomRight;
    private Supplier<Integer> shadeCorners;

    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiRectangle(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiRectangle border(int border) {
        return border(() -> border);
    }

    public GuiRectangle border(Supplier<Integer> border) {
        this.border = border;
        return this;
    }

    public GuiRectangle fill(int fill) {
        return fill(() -> fill);
    }

    public GuiRectangle fill(Supplier<Integer> fill) {
        this.fill = fill;
        return this;
    }

    public GuiRectangle rectangle(int fill, int border) {
        return rectangle(() -> fill, () -> border);
    }

    public GuiRectangle rectangle(Supplier<Integer> fill, Supplier<Integer> border) {
        this.fill = fill;
        this.border = border;
        return this;
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> fill);
    }

    public GuiRectangle shadedRect(Supplier<Integer> topLeft, Supplier<Integer> bottomRight, Supplier<Integer> fill) {
        return shadedRect(topLeft, bottomRight, () -> GuiRender.midColour(topLeft.get(), bottomRight.get()), fill);
    }

    public GuiRectangle shadedRect(int topLeft, int bottomRight, int cornerMix, int fill) {
        return shadedRect(() -> topLeft, () -> bottomRight, () -> cornerMix, () -> fill);
    }

    public GuiRectangle shadedRect(Supplier<Integer> topLeft, Supplier<Integer> bottomRight, Supplier<Integer> cornerMix, Supplier<Integer> fill) {
        this.fill = fill;
        this.shadeTopLeft = topLeft;
        this.shadeBottomRight = bottomRight;
        this.shadeCorners = cornerMix;
        return this;
    }

    public GuiRectangle borderWidth(int borderWidth) {
        return borderWidth(() -> borderWidth);
    }

    public GuiRectangle borderWidth(Supplier<Integer> borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public int getBorderWidth() {
        return borderWidth.get();
    }

    @Override
    public void renderBehind(GuiRender render) {
        if (shadeTopLeft != null && shadeBottomRight != null && shadeCorners != null) {
            render.shadedRect(getRectangle(), getBorderWidth(), shadeTopLeft.get(), shadeBottomRight.get(), shadeCorners.get(), fill == null ? 0 : fill.get());
        } else if (border != null){
            render.borderRect(getRectangle(), getBorderWidth(), fill == null ? 0 : fill.get(), border.get());
        } else if (fill != null) {
            render.rect(getRectangle(), fill.get());
        }
    }
}
