package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;

/**
 * A translucent highlight rectangle shown as a drop-zone indicator during drag operations.
 * Starts hidden; call {@link #show()} / {@link #hide()} to toggle visibility.
 */
public class GuiDropZone extends GuiRectangle {

    public GuiDropZone(GuiParent<?> parent) {
        super(parent);
        fill(0x4400AAFF);  // translucent blue
        setEnabled(false); // hidden by default
    }

    public void show() {
        setEnabled(true);
    }

    public void hide() {
        setEnabled(false);
    }

    public GuiDropZone setHighlightColor(int argb) {
        fill(argb);
        return this;
    }
}
