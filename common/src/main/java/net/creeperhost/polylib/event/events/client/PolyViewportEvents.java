package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Camera;

/**
 * Client-side viewport events: fog, FOV, and camera distance.
 *
 * <p>All events in this class are <b>NeoForge-primary</b> — no Fabric native equivalents
 * exist.  Fabric mixin bridges would require hooking into deep rendering internals;
 * they may be added in a future tier.
 *
 * <p>Note: Camera-angle modification is covered by
 * {@link PolyCameraEvents#CAMERA_SETUP} (already wired in T16).
 * FOV <em>modifier</em> (multiplier) is covered by
 * {@link PolyCameraEvents#FOV_MODIFIER} (already wired in T16).
 */
public final class PolyViewportEvents
{
    /**
     * Fired to allow modification of the fog color.
     * Mutate {@code rgb[0..2]} (red, green, blue) to override the fog color.
     * <p>
     * NeoForge: {@code ViewportEvent.ComputeFogColor}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<ComputeFogColor> COMPUTE_FOG_COLOR = PolyEvent.create(
            handlers -> (camera, rgb) -> handlers.forEach(h -> h.onComputeFogColor(camera, rgb)));

    /**
     * Fired to allow modification of fog density and extent.
     * Mutate {@code params[0]} (near plane) and {@code params[1]} (far plane).
     * <p>
     * NeoForge: {@code ViewportEvent.RenderFog}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<RenderFog> RENDER_FOG = PolyEvent.create(
            handlers -> (camera, params) -> handlers.forEach(h -> h.onRenderFog(camera, params)));

    /**
     * Fired to allow modification of the computed (absolute) field-of-view angle.
     * Mutate {@code fov[0]} to override the FOV (in degrees).
     * <p>
     * This is the <em>absolute</em> FOV, distinct from the FOV modifier/multiplier
     * in {@link PolyCameraEvents#FOV_MODIFIER}.
     * <p>
     * NeoForge: {@code ViewportEvent.ComputeFov}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<ComputeFov> COMPUTE_FOV = PolyEvent.create(
            handlers -> fov -> handlers.forEach(h -> h.onComputeFov(fov)));

    /**
     * Fired to allow modification of the third-person (detached) camera distance.
     * Mutate {@code distance[0]} to override the distance.
     * <p>
     * NeoForge: {@code CalculateDetachedCameraDistanceEvent}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<ComputeCameraDistance> COMPUTE_CAMERA_DISTANCE = PolyEvent.create(
            handlers -> distance -> handlers.forEach(h -> h.onComputeCameraDistance(distance)));

    private PolyViewportEvents() {}

    /**
     * Callback for mutating the computed fog color.
     */
    @FunctionalInterface
    public interface ComputeFogColor
    {
        /** @param rgb float[3] — {red, green, blue}; mutate to override fog color. */
        void onComputeFogColor(Camera camera, float[] rgb);
    }

    /**
     * Callback for mutating fog near and far planes.
     */
    @FunctionalInterface
    public interface RenderFog
    {
        /** @param params float[2] — {nearPlane, farPlane}; mutate to override fog distances. */
        void onRenderFog(Camera camera, float[] params);
    }

    /**
     * Callback for mutating the absolute field of view.
     */
    @FunctionalInterface
    public interface ComputeFov
    {
        /** @param fov float[1] — {fieldOfView} in degrees; mutate to override. */
        void onComputeFov(float[] fov);
    }

    /**
     * Callback for mutating third-person camera distance.
     */
    @FunctionalInterface
    public interface ComputeCameraDistance
    {
        /** @param distance float[1] — {cameraDistance}; mutate to override third-person distance. */
        void onComputeCameraDistance(float[] distance);
    }
}
