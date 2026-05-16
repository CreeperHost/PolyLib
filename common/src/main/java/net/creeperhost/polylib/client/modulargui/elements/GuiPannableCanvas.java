package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import java.util.LinkedHashMap;

/**
 * A generic panning/zooming canvas element.
 * 
 * Provides:
 * - panX, panY, zoom state
 * - Coordinate transformations between Screen Space and Canvas Space
 * - Middle-mouse dragging to pan
 * - Scroll wheel to zoom toward cursor
 * - Bookmark saving/recalling for viewport positions
 */
public class GuiPannableCanvas<T extends GuiPannableCanvas<T>> extends GuiElement<T> {

    protected double panX = 0;
    protected double panY = 0;
    protected double zoom = 1.0;

    protected boolean panning = false;
    protected double panMouseStartX, panMouseStartY;
    protected double panStartX, panStartY;

    public final LinkedHashMap<String, double[]> BOOKMARKS = new LinkedHashMap<>();

    public GuiPannableCanvas(GuiParent<?> parent) {
        super(parent);
    }

    // ── Coordinate Conversion ──────────────────────────────────────────────────

    public double canvasToScreenX(double cx) { return xMin() + panX + cx * zoom; }
    public double canvasToScreenY(double cy) { return yMin() + panY + cy * zoom; }

    public double screenToCanvasX(double sx) { return (sx - xMin() - panX) / zoom; }
    public double screenToCanvasY(double sy) { return (sy - yMin() - panY) / zoom; }

    /** Returns scaled pixel size for a canvas-unit value at current zoom. */
    public double z(double v) { return v * zoom; }

    // ── Viewport Queries ───────────────────────────────────────────────────────

    public double viewCenterCanvasX() {
        return (xSize() / 2.0 - panX) / zoom;
    }

    public double viewCenterCanvasY() {
        return (ySize() / 2.0 - panY) / zoom;
    }

    public void setZoom(double z) {
        this.zoom = Math.max(0.25, Math.min(3.0, z));
    }

    public double getZoom() { return zoom; }

    // ── Bookmarks ─────────────────────────────────────────────────────────────

    public void saveBookmark(String name) {
        BOOKMARKS.put(name, new double[]{panX, panY, zoom});
    }

    public void recallBookmark(String name) {
        double[] b = BOOKMARKS.get(name);
        if (b != null) {
            panX = b[0];
            panY = b[1];
            zoom = b[2];
        }
    }

    // ── Input Handling (Pan & Zoom) ───────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        double factor = scrollY > 0 ? 1.15 : (1.0 / 1.15);
        double newZoom = Math.max(0.25, Math.min(3.0, zoom * factor));
        
        // Zoom toward cursor
        double cx = mouseX - xMin();
        double cy = mouseY - yMin();
        panX = cx - (cx - panX) * (newZoom / zoom);
        panY = cy - (cy - panY) * (newZoom / zoom);
        zoom = newZoom;
        
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 2 && isMouseOver(mouseX, mouseY)) {
            panning = true;
            panMouseStartX = mouseX;
            panMouseStartY = mouseY;
            panStartX = panX;
            panStartY = panY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 2 && panning) {
            panning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (panning) {
            panX = panStartX + (mouseX - panMouseStartX);
            panY = panStartY + (mouseY - panMouseStartY);
        }
        super.mouseMoved(mouseX, mouseY);
    }
}
