package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Geometric helpers for Scratch-style block snapping interactions.
 */
public final class SnapZoneHelper {
    private SnapZoneHelper() {}

    /** Returns the Y coordinate of the snap zone at the bottom of a block. */
    public static double snapBumpY(double blockY, double blockH) {
        return blockY + blockH;
    }

    /** Returns the Y coordinate of the snap zone at the top of a block (notch). */
    public static double snapNotchY(double blockY) {
        return blockY;
    }

    /** Returns true if a point is within snap proximity of a notch/bump. */
    public static boolean isInSnapZone(double dropX, double dropY,
                                        double targetX, double targetY,
                                        double targetW, double snapRadius) {
        return dropX >= targetX - snapRadius &&
               dropX <= targetX + targetW + snapRadius &&
               Math.abs(dropY - targetY) < snapRadius;
    }
}
