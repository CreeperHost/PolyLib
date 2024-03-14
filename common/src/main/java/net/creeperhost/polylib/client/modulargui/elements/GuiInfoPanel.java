package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.ForegroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.*;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.RIGHT;

/**
 * Created by brandon3055 on 06/01/2024
 */
public class GuiInfoPanel extends GuiElement<GuiInfoPanel> implements ForegroundRender {

    private int minWidth = 24;
    private int minHeight = 24;
    private int maxWidth = 80;
    private int maxHeight = 120;
    private double expandAnim = 0;
    private double animSpeed = 0.1;
    private Material closedIcon = null;
    private int iconWidth;
    private int iconHeight;
    private Borders borders = new Borders(4, 4, 4, 4);
    private boolean expanded = false;

    private GuiScrolling scrollElement = null;
    private GuiSlider scrollBar = null;

    public GuiInfoPanel(@NotNull GuiParent<?> parent) {
        super(parent);
        jeiExclude();
        constrain(GeoParam.WIDTH, Constraint.dynamic(() -> minWidth + ((maxWidth - minWidth) * expandAnim)));
        constrain(GeoParam.HEIGHT, Constraint.dynamic(() -> minHeight + ((maxHeight - minHeight) * expandAnim)));
    }

    public GuiInfoPanel setBackground(Function<GuiInfoPanel, GuiElement<?>> backgroundBuilder) {
        return setBackground(backgroundBuilder.apply(this));
    }

    public GuiInfoPanel setBackground(GuiElement<?> background) {
        if (background.getParent() != this) throw new IllegalStateException("Background parent must be the parent GuiInfoPanel");
        if (scrollElement != null) throw new IllegalStateException("Background must be set before content is added");
        Constraints.bind(background, this);
        return this;
    }

    public GuiInfoPanel setMinSize(int minWidth, int minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        return this;
    }

    public GuiInfoPanel setMaxSize(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        return this;
    }

    public GuiInfoPanel setIcon(Material closedIcon, int iconWidth, int iconHeight) {
        this.closedIcon = closedIcon;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        return this;
    }

    public GuiInfoPanel setAnimSpeed(double animSpeed) {
        this.animSpeed = animSpeed;
        return this;
    }

    public GuiInfoPanel setBorders(Borders borders) {
        this.borders = borders;
        return this;
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver() && button == GuiButton.LEFT_CLICK) {
            expanded = !expanded;
            return true;
        }
        return false;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        if (expanded && expandAnim < 1) expandAnim = Math.min(expandAnim + animSpeed, 1);
        if (!expanded && expandAnim > 0) expandAnim = Math.max(expandAnim - animSpeed, 0);
    }

    protected void initScrollElement() {
        if (scrollElement != null) return;

        scrollElement = new GuiScrolling(this)
                .setEnabled(() -> expanded && expandAnim == 1);
        Constraints.bind(scrollElement, this, borders);
        scrollElement.getContentElement().constrain(RIGHT, match(scrollElement.get(RIGHT)));

        scrollBar = new GuiSlider(this, Axis.Y)
                .setEnabled(() -> expanded && expandAnim == 1 && scrollElement.hiddenSize(Axis.Y) > 0)
                .setSliderState(scrollElement.scrollState(Axis.Y))
                .setScrollableElement(this)
                .constrain(TOP, match(scrollElement.get(TOP)))
                .constrain(LEFT, match(scrollElement.get(RIGHT)))
                .constrain(BOTTOM, match(scrollElement.get(BOTTOM)))
                .constrain(WIDTH, dynamic(borders::right));

        GuiRectangle bar = new GuiRectangle(scrollBar.getSlider())
                .fill(0x30FFFFFF);
        Constraints.bind(bar, scrollBar.getSlider());
        bar.constrain(RIGHT, null).constrain(WIDTH, literal(1));
    }

    public GuiScrolling getScrollElement() {
        initScrollElement();
        return scrollElement;
    }

    public GuiElement<?> getContentElement() {
        return getScrollElement().getContentElement();
    }

    public GuiSlider getScrollBar() {
        return scrollBar;
    }

    @Override
    public void renderInFront(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (expandAnim < 1 && closedIcon != null) {
            render.texRect(closedIcon, xCenter() - (iconWidth / 2D), yCenter() - (iconHeight / 2D), iconWidth, iconHeight, 1F, 1F, 1F, 1F - (float) expandAnim);
//            render.texRect(closedIcon, xMin() + (minWidth / 2D) - (iconWidth / 2D), yMin() + (minHeight / 2D) - (iconHeight / 2D), iconWidth, iconHeight, 1F, 1F, 1F, 1F - (float) expandAnim);
        }
    }

    public static GuiInfoPanel basicInfoPanel(GuiElement<?> parent, Function<GuiInfoPanel, GuiElement<?>> backgroundBuilder, Component title, Component... textTiles) {
        return basicInfoPanel(parent, backgroundBuilder, title, List.of(textTiles), Align.LEFT);
    }

    public static GuiInfoPanel basicInfoPanel(GuiElement<?> parent, Function<GuiInfoPanel, GuiElement<?>> backgroundBuilder, Component title, List<Component> textTiles, Align align) {
        GuiInfoPanel panel = new GuiInfoPanel(parent);
        panel.setBackground(backgroundBuilder);
        GuiElement<?> content = panel.getContentElement();

        GuiText titleText = new GuiText(content, title)
                .constrain(TOP, match(content.get(TOP)))
                .constrain(LEFT, match(content.get(LEFT)))
                .constrain(RIGHT, match(content.get(RIGHT)))
                .setWrap(true)
                .autoHeight();

        GuiText last = null;
        for (Component comp : textTiles) {
            last = new GuiText(content, comp)
                    .setAlignment(align)
                    .constrain(TOP, last == null ? relative(titleText.get(BOTTOM), 4) : relative(last.get(BOTTOM), 1))
                    .constrain(LEFT, match(content.get(LEFT)))
                    .constrain(RIGHT, match(content.get(RIGHT)))
                    .setWrap(true)
                    .autoHeight();
        }

        return panel;
    }
}
