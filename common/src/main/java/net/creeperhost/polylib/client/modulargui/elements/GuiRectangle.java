package net.creeperhost.polylib.client.modulargui.elements;

import com.google.common.base.Suppliers;
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

    private Supplier<Integer> borderWidth = Suppliers.memoize(() -> 1);

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
        return border(Suppliers.memoize(() -> border));
    }

    public GuiRectangle border(Supplier<Integer> border) {
        this.border = border;
        return this;
    }

    public GuiRectangle fill(int fill) {
        return fill(Suppliers.memoize(() -> fill));
    }

    public GuiRectangle fill(Supplier<Integer> fill) {
        this.fill = fill;
        return this;
    }

    public GuiRectangle rectangle(int fill, int border) {
        return rectangle(Suppliers.memoize(() -> fill), Suppliers.memoize(() -> border));
    }

    public GuiRectangle rectangle(Supplier<Integer> fill, Supplier<Integer> border) {
        this.fill = fill;
        this.border = border;
        return this;
    }

    public GuiRectangle shadedRect(int fill, int topLeft, int bottomRight) {
        return shadedRect(Suppliers.memoize(() -> fill), Suppliers.memoize(() -> topLeft), Suppliers.memoize(() -> bottomRight));
    }

    public GuiRectangle shadedRect(Supplier<Integer> fill, Supplier<Integer> topLeft, Supplier<Integer> bottomRight) {
        return shadedRect(fill, topLeft, bottomRight, () -> GuiRender.midColour(topLeft.get(), bottomRight.get()));
    }

    public GuiRectangle shadedRect(int fill, int topLeft, int bottomRight, int cornerMix) {
        return shadedRect(Suppliers.memoize(() -> fill), Suppliers.memoize(() -> topLeft), Suppliers.memoize(() -> bottomRight), Suppliers.memoize(() -> cornerMix));
    }

    public GuiRectangle shadedRect(Supplier<Integer> fill, Supplier<Integer> topLeft, Supplier<Integer> bottomRight, Supplier<Integer> cornerMix) {
        this.fill = fill;
        this.shadeTopLeft = topLeft;
        this.shadeBottomRight = bottomRight;
        this.shadeCorners = cornerMix;
        return this;
    }

    public GuiRectangle borderWidth(int borderWidth) {
        return borderWidth(Suppliers.memoize(() -> borderWidth));
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
