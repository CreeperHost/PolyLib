package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.player.LocalPlayer;

public final class PolyCameraEvents
{
    /**
     * Fired when computing the FOV modifier for the camera.
     * Modify {@code fovHolder[0]} to override the FOV multiplier.
     * <p>
     * NeoForge: {@code ComputeFovModifierEvent}<br>
     * Fabric: mixin into {@code GameRenderer#getFov}
     */
    public static final PolyEvent<FovModifier> FOV_MODIFIER = PolyEvent.create(
            handlers -> (player, fovHolder) -> handlers.forEach(h -> h.onComputeFov(player, fovHolder)));

    /**
     * Fired when computing the camera angles (yaw, pitch, roll).
     * Use {@link CameraContext} to read or modify the camera orientation.
     * <p>
     * NeoForge: {@code ViewportEvent.ComputeCameraAngles}<br>
     * Fabric: mixin into camera angle computation
     */
    public static final PolyEvent<CameraSetup> CAMERA_SETUP = PolyEvent.create(
            handlers -> ctx -> handlers.forEach(h -> h.onCameraSetup(ctx)));

    private PolyCameraEvents() {}

    @FunctionalInterface
    public interface FovModifier
    {
        void onComputeFov(LocalPlayer player, float[] fovHolder);
    }

    @FunctionalInterface
    public interface CameraSetup
    {
        void onCameraSetup(CameraContext ctx);
    }

    /** Mutable context for camera yaw, pitch, and roll. */
    public static final class CameraContext
    {
        private float yaw;
        private float pitch;
        private float roll;

        public CameraContext(float yaw, float pitch, float roll)
        {
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getYaw() { return yaw; }

        public void setYaw(float yaw) { this.yaw = yaw; }

        public float getPitch() { return pitch; }

        public void setPitch(float pitch) { this.pitch = pitch; }

        public float getRoll() { return roll; }

        public void setRoll(float roll) { this.roll = roll; }
    }
}
