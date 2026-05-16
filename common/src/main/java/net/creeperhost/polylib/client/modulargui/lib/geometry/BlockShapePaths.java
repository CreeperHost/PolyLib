package net.creeperhost.polylib.client.modulargui.lib.geometry;

import net.creeperhost.polylib.client.modulargui.lib.GuiRender;

/**
 * Renders Scratch-style visual programming block shapes using PolyLib's GuiRender primitives.
 *
 * <p>All dimensions are in screen pixels. The caller is responsible for translating
 * canvas-space positions to screen-space before calling these methods.</p>
 */
public final class BlockShapePaths {

    private BlockShapePaths() {}

    // ── Notch/bump geometry ───────────────────────────────────────────────────
    /** Width of the notch/bump tab (pixels at zoom=1). */
    public static final int NOTCH_W = 20;
    /** Height of the notch/bump tab. */
    public static final int NOTCH_H = 4;
    /** Horizontal offset of the notch from the left edge. */
    public static final int NOTCH_INSET = 20;
    /** Corner radius for rounded shapes. */
    public static final int CORNER_R = 4;
    /** Height of the C-block mouth bar (bottom of the mouth). */
    public static final int CBAR_H = 8;

    // ── HAT block: rounded top, bump bottom ──────────────────────────────────
    public static void drawHatBlock(GuiRender render, double x, double y,
                                     double w, double h, int body, int border) {
        // Main body (slightly inset top for the rounded look)
        render.fill(x, y + CORNER_R, x + w, y + h, body);
        // Top rounded portion (approximated with a slightly narrower rect)
        render.fill(x + CORNER_R, y, x + w - CORNER_R, y + CORNER_R, body);
        // Top-left and top-right corner fills
        render.fill(x + 1, y + 1, x + CORNER_R, y + CORNER_R, body);
        render.fill(x + w - CORNER_R, y + 1, x + w - 1, y + CORNER_R, body);

        // Bump at bottom (protruding tab that fits into next block's notch)
        double bx = x + NOTCH_INSET;
        render.fill(bx, y + h, bx + NOTCH_W, y + h + NOTCH_H, body);

        // Border outline
        drawOutline(render, x, y, w, h, border);
        // Top border: curved approximation
        render.fill(x + CORNER_R, y, x + w - CORNER_R, y + 1, border);
        render.fill(x, y + CORNER_R, x + 1, y + CORNER_R + 1, border);
        render.fill(x + w - 1, y + CORNER_R, x + w, y + CORNER_R + 1, border);
    }

    // ── STACK block: notch top, bump bottom ──────────────────────────────────
    public static void drawStackBlock(GuiRender render, double x, double y,
                                       double w, double h, int body, int border) {
        // Main body
        render.fill(x, y, x + w, y + h, body);

        // Notch cutout at top (darker area simulating the indent)
        double nx = x + NOTCH_INSET;
        render.fill(nx, y, nx + NOTCH_W, y + NOTCH_H, darkenSlightly(body));

        // Bump at bottom
        double bx = x + NOTCH_INSET;
        render.fill(bx, y + h, bx + NOTCH_W, y + h + NOTCH_H, body);

        // Border
        drawOutline(render, x, y, w, h, border);
    }

    // ── REPORTER block: oval (rounded ends) ──────────────────────────────────
    public static void drawReporterBlock(GuiRender render, double x, double y,
                                          double w, double h, int body, int border) {
        double r = h / 2.0; // semicircular ends
        // Main body (rectangular middle section)
        render.fill(x + r, y, x + w - r, y + h, body);
        // Left rounded cap (approximated with filled rectangles)
        drawRoundedCap(render, x, y, r, h, body, true);
        // Right rounded cap
        drawRoundedCap(render, x + w - r, y, r, h, body, false);

        // Border: top and bottom edges of the middle section
        render.fill(x + r, y, x + w - r, y + 1, border);
        render.fill(x + r, y + h - 1, x + w - r, y + h, border);
        // Left/right arc borders
        drawRoundedCapBorder(render, x, y, r, h, border, true);
        drawRoundedCapBorder(render, x + w - r, y, r, h, border, false);
    }

    // ── BOOLEAN block: hexagonal (pointed ends) ──────────────────────────────
    public static void drawBooleanBlock(GuiRender render, double x, double y,
                                         double w, double h, int body, int border) {
        double pointW = h / 2.0; // width of the pointed ends
        // Main body (center section)
        render.fill(x + pointW, y, x + w - pointW, y + h, body);

        // Left point (triangle approximated with progressively narrower rects)
        for (int i = 0; i < (int) pointW; i++) {
            double t = i / pointW;
            double py = y + h / 2.0 - (h / 2.0) * (1.0 - t);
            double ph = h * (1.0 - t);
            render.fill(x + i, py, x + i + 1, py + ph, body);
        }

        // Right point (mirror of left)
        for (int i = 0; i < (int) pointW; i++) {
            double t = i / pointW;
            double py = y + h / 2.0 - (h / 2.0) * (1.0 - t);
            double ph = h * (1.0 - t);
            render.fill(x + w - i - 1, py, x + w - i, py + ph, body);
        }

        // Border edges
        render.fill(x + pointW, y, x + w - pointW, y + 1, border);
        render.fill(x + pointW, y + h - 1, x + w - pointW, y + h, border);
    }

    // ── C-BLOCK: C-shaped wrap ───────────────────────────────────────────────
    public static void drawCBlock(GuiRender render, double x, double y,
                                   double w, double h, int body, int border) {
        int headerH = 28;
        int mouthInset = 20;
        int footerH = CBAR_H;
        double mouthTop = y + headerH;
        double mouthBot = y + h - footerH;
        double mouthLeft = x + mouthInset;

        // Header section
        render.fill(x, y, x + w, y + headerH, body);
        // Notch at top
        double nx = x + NOTCH_INSET;
        render.fill(nx, y, nx + NOTCH_W, y + NOTCH_H, darkenSlightly(body));

        // Left spine (runs full height)
        render.fill(x, y + headerH, x + mouthInset, y + h, body);

        // Footer bar
        render.fill(x, y + h - footerH, x + w, y + h, body);

        // Bump at bottom
        double bx = x + NOTCH_INSET;
        render.fill(bx, y + h, bx + NOTCH_W, y + h + NOTCH_H, body);

        // Mouth bump at top of mouth (so inner blocks snap in)
        render.fill(mouthLeft + NOTCH_INSET, mouthTop,
                    mouthLeft + NOTCH_INSET + NOTCH_W, mouthTop + NOTCH_H, body);

        // Mouth area background (slightly darker to show the "inside")
        render.fill(mouthLeft, mouthTop, x + w, mouthBot, darkenSlightly(darkenSlightly(body)));

        // Border
        drawOutline(render, x, y, w, h, border);
        // Mouth border lines
        render.fill(mouthLeft, mouthTop, x + w, mouthTop + 1, border);      // mouth top edge
        render.fill(mouthLeft, mouthBot - 1, x + w, mouthBot, border);      // mouth bottom edge
        render.fill(mouthLeft, mouthTop, mouthLeft + 1, mouthBot, border);  // mouth left edge
    }

    // ── CAP block: notch top, flat bottom ────────────────────────────────────
    public static void drawCapBlock(GuiRender render, double x, double y,
                                     double w, double h, int body, int border) {
        // Main body
        render.fill(x, y, x + w, y + h, body);

        // Notch cutout at top
        double nx = x + NOTCH_INSET;
        render.fill(nx, y, nx + NOTCH_W, y + NOTCH_H, darkenSlightly(body));

        // No bump at bottom (cap = terminator)
        // Flat bottom with slightly thicker border to signal "end"
        render.fill(x, y + h - 2, x + w, y + h, border);

        // Border
        drawOutline(render, x, y, w, h, border);
    }

    // ── Shared drawing helpers ────────────────────────────────────────────────

    /** Draws a 1px outline rectangle. */
    private static void drawOutline(GuiRender render, double x, double y,
                                     double w, double h, int color) {
        render.fill(x, y, x + w, y + 1, color);         // top
        render.fill(x, y + h - 1, x + w, y + h, color); // bottom
        render.fill(x, y, x + 1, y + h, color);         // left
        render.fill(x + w - 1, y, x + w, y + h, color); // right
    }

    /** Draws an approximated rounded cap (semicircle fill). */
    private static void drawRoundedCap(GuiRender render, double x, double y,
                                        double r, double h, int color, boolean isLeft) {
        int steps = Math.max(4, (int) r);
        for (int i = 0; i < steps; i++) {
            double t = (i + 0.5) / steps;
            double angle = Math.PI * t;
            double capW = Math.sin(angle) * r;
            double capY = y + t * h;
            double capH = h / steps;
            if (isLeft) {
                render.fill(x + r - capW, capY, x + r, capY + capH, color);
            } else {
                render.fill(x, capY, x + capW, capY + capH, color);
            }
        }
    }

    /** Draws the border pixels around a rounded cap. */
    private static void drawRoundedCapBorder(GuiRender render, double x, double y,
                                              double r, double h, int color, boolean isLeft) {
        int steps = Math.max(8, (int) (r * 2));
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double angle = Math.PI * t;
            double cx = isLeft ? x + r - Math.sin(angle) * r : x + Math.sin(angle) * r;
            double cy = y + t * h;
            render.fill(cx - 0.5, cy - 0.5, cx + 0.5, cy + 0.5, color);
        }
    }

    /** Darkens a colour slightly for depth effects. */
    private static int darkenSlightly(int argb) {
        float factor = 0.7f;
        int a = (argb >> 24) & 0xFF;
        int r = (int) (((argb >> 16) & 0xFF) * factor);
        int g = (int) (((argb >> 8) & 0xFF) * factor);
        int b = (int) ((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
