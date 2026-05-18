package net.creeperhost.polylib.chat.layout;

/**
 * Screen corner a floating window is snapped to.
 * When snapped, the window re-anchors on screen resize.
 */
public enum SnapCorner {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    private static final int MARGIN = 4;

    /**
     * Compute the top-left position for a window of the given size, snapped to this corner.
     * @return int[2] = {x, y}
     */
    public int[] computePosition(int windowWidth, int windowHeight, int screenWidth, int screenHeight) {
        return switch (this) {
            case TOP_LEFT     -> new int[]{ MARGIN, MARGIN };
            case TOP_RIGHT    -> new int[]{ screenWidth - windowWidth - MARGIN, MARGIN };
            case BOTTOM_LEFT  -> new int[]{ MARGIN, screenHeight - windowHeight - MARGIN };
            case BOTTOM_RIGHT -> new int[]{ screenWidth - windowWidth - MARGIN, screenHeight - windowHeight - MARGIN };
        };
    }

    /**
     * Determine which corner (if any) a point is closest to, within threshold pixels.
     * @return the closest SnapCorner, or null if none within threshold
     */
    public static SnapCorner detect(double centerX, double centerY, int screenWidth, int screenHeight, int threshold) {
        if (centerX < threshold && centerY < threshold) return TOP_LEFT;
        if (centerX > screenWidth - threshold && centerY < threshold) return TOP_RIGHT;
        if (centerX < threshold && centerY > screenHeight - threshold) return BOTTOM_LEFT;
        if (centerX > screenWidth - threshold && centerY > screenHeight - threshold) return BOTTOM_RIGHT;
        return null;
    }
}
