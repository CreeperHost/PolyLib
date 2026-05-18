package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;

/**
 * A draggable, floating window primitive built on GuiManipulable.
 * Supports window titles, close buttons, and tab-group docking logic.
 */
public class GuiWindow extends GuiManipulable {

    private final GuiRectangle headerBar;
    private final GuiText titleText;
    private final GuiButton closeButton;
    private final GuiRectangle background;

    public GuiWindow(GuiParent<?> parent) {
        super(parent);
        
        // Background for the entire window
        this.background = new GuiRectangle(getContentElement());
        this.background.constrain(GeoParam.LEFT, Constraint.match(getContentElement().get(GeoParam.LEFT)))
                       .constrain(GeoParam.RIGHT, Constraint.match(getContentElement().get(GeoParam.RIGHT)))
                       .constrain(GeoParam.TOP, Constraint.match(getContentElement().get(GeoParam.TOP)))
                       .constrain(GeoParam.BOTTOM, Constraint.match(getContentElement().get(GeoParam.BOTTOM)))
                       .fill(0xDD202020);

        // Header bar (used for dragging)
        this.headerBar = new GuiRectangle(getContentElement());
        this.headerBar.constrain(GeoParam.LEFT, Constraint.match(getContentElement().get(GeoParam.LEFT)))
                      .constrain(GeoParam.RIGHT, Constraint.match(getContentElement().get(GeoParam.RIGHT)))
                      .constrain(GeoParam.TOP, Constraint.match(getContentElement().get(GeoParam.TOP)))
                      .constrain(GeoParam.HEIGHT, Constraint.literal(14))
                      .fill(0xFF333333);

        this.titleText = new GuiText(headerBar);
        this.titleText.constrain(GeoParam.LEFT, Constraint.relative(headerBar.get(GeoParam.LEFT), 4))
                      .constrain(GeoParam.TOP, Constraint.match(headerBar.get(GeoParam.TOP)))
                      .constrain(GeoParam.BOTTOM, Constraint.match(headerBar.get(GeoParam.BOTTOM)))
                      .setShadow(true);

        this.closeButton = new GuiButton(headerBar);
        this.closeButton.constrain(GeoParam.RIGHT, Constraint.relative(headerBar.get(GeoParam.RIGHT), -2))
                        .constrain(GeoParam.TOP, Constraint.relative(headerBar.get(GeoParam.TOP), 2))
                        .constrain(GeoParam.WIDTH, Constraint.literal(10))
                        .constrain(GeoParam.HEIGHT, Constraint.literal(10));
        // Right edge of title stops just before the close button
        this.titleText.constrain(GeoParam.RIGHT, Constraint.relative(this.closeButton.get(GeoParam.LEFT), -2));
        GuiText closeLabel = new GuiText(this.closeButton, net.minecraft.network.chat.Component.literal("x"));
        closeLabel.constrain(GeoParam.LEFT, Constraint.match(this.closeButton.get(GeoParam.LEFT)))
                  .constrain(GeoParam.TOP, Constraint.match(this.closeButton.get(GeoParam.TOP)))
                  .constrain(GeoParam.RIGHT, Constraint.match(this.closeButton.get(GeoParam.RIGHT)))
                  .constrain(GeoParam.BOTTOM, Constraint.match(this.closeButton.get(GeoParam.BOTTOM)));
        this.closeButton.setLabel(closeLabel);

        // Use the header bar as the drag handle
        this.setMoveHandle(headerBar);
        this.addResizeHandles(4, false); // Bottom, Left, Right resizing
        this.enableCursors(true);
    }

    public GuiWindow setTitle(net.minecraft.network.chat.Component title) {
        this.titleText.setText(title);
        return this;
    }

    public GuiButton getCloseButton() {
        return closeButton;
    }

    public GuiRectangle getBackground() {
        return background;
    }

    public GuiRectangle getHeaderBar() {
        return headerBar;
    }
}
