package net.creeperhost.polylib.chat.client;

/**
 * Generates a pulsing ARGB color that cycles between two colors over time.
 * Used for mention pulse on floating window headers and tab badges.
 */
public class PulseEffect {

    private static final int COLOR_A = 0xFFFFAA00; // yellow-orange
    private static final int COLOR_B = 0xFFFF6600; // deep orange
    private static final int CYCLE_TICKS = 20;     // full cycle = 1 second

    private int tickCounter = 0;

    /** Call once per client tick while the pulse is active. */
    public void tick() {
        tickCounter++;
    }

    /** Reset the animation (e.g. when pulse starts). */
    public void reset() {
        tickCounter = 0;
    }

    /** Get the current interpolated ARGB color. */
    public int getColor() {
        return colorForTick(tickCounter);
    }

    /** Static convenience: get pulse color for a given tick count. */
    public static int colorForTick(int tick) {
        double phase = (tick % CYCLE_TICKS) / (double) CYCLE_TICKS;
        double t = phase < 0.5 ? phase * 2.0 : 2.0 - phase * 2.0;
        return lerpColor(COLOR_A, COLOR_B, t);
    }

    private static int lerpColor(int c1, int c2, double t) {
        int a1 = (c1 >> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int a2 = (c2 >> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
