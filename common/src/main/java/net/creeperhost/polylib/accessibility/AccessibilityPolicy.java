package net.creeperhost.polylib.accessibility;

/**
 * Controls how a registered accessibility preference interacts with server configuration.
 */
public enum AccessibilityPolicy
{
    /**
     * Default. The player's value is stored and applied server-side regardless of any server
     * configuration. Use this for gameplay-safety toggles (fall rescue, void grave, etc.) where
     * overriding the player's choice would be harmful.
     */
    PLAYER_OVERRIDES_SERVER,

    /**
     * Server configuration may clamp or override the player's preference at runtime.
     * Use for purely cosmetic or input-mode toggles.
     * <p>
     * Note: radical accessibility is always active in PolyLib, so in practice
     * all PolyLib-registered preferences are treated as {@link #PLAYER_OVERRIDES_SERVER}.
     */
    RESPECTS_SERVER_CONFIG
}
