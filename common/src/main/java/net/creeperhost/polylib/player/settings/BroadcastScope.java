package net.creeperhost.polylib.player.settings;

/**
 * Controls which clients receive S2C sync packets when a PlayerClientSetting value changes.
 */
public enum BroadcastScope
{
    /**
     * Stored server-side only. Never sent to any client.
     * Use for server-computed preferences that don't affect rendering.
     */
    SERVER_ONLY,

    /**
     * Sent to the owning player only (e.g. per-player UI state, accessibility prefs).
     */
    SELF_ONLY,

    /**
     * Sent to all players currently online when changed.
     * Also sent to newly joining players (full backfill on login).
     * Use when all players need the data regardless of proximity.
     */
    ALL_ONLINE,

    /**
     * Sent only to players currently tracking the owner (within render/entity range).
     * Backfill is automatic via StartTracking — no explicit login sync needed.
     * Recommended for appearance/cosmetic data. More efficient than ALL_ONLINE.
     */
    TRACKING_RANGE
}
