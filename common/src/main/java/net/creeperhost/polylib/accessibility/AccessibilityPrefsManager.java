package net.creeperhost.polylib.accessibility;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side store for per-player accessibility preferences.
 * <p>
 * Pure common — no loader API. Thread-safe via {@link ConcurrentHashMap}.
 * Values are stored exactly as received from the client; no server config gate is applied here.
 * The policy check ({@link AccessibilityPolicy}) is enforced on the client before sending.
 */
public final class AccessibilityPrefsManager
{
    private AccessibilityPrefsManager() {}

    private static final ConcurrentHashMap<UUID, Map<String, Boolean>> STORE = new ConcurrentHashMap<>();

    /**
     * Applies a batch of preference values from a client packet.
     * Overwrites only the provided keys; other keys are left unchanged.
     *
     * @param playerUUID UUID of the player who sent the packet
     * @param values     Map of preference key → value as sent by the client
     */
    public static void applyFromClient(UUID playerUUID, Map<String, Boolean> values)
    {
        STORE.computeIfAbsent(playerUUID, id -> new ConcurrentHashMap<>()).putAll(values);
    }

    /**
     * Sets a single preference value directly (e.g. from a server-side command or admin tool).
     */
    public static void set(UUID playerUUID, String key, boolean value)
    {
        STORE.computeIfAbsent(playerUUID, id -> new ConcurrentHashMap<>()).put(key, value);
    }

    /**
     * Returns the player's current value for the given key, or {@code defaultValue} if
     * no value has been stored (player hasn't sent prefs yet, or has since logged out).
     */
    public static boolean getOrDefault(UUID playerUUID, String key, boolean defaultValue)
    {
        Map<String, Boolean> prefs = STORE.get(playerUUID);
        if (prefs == null) return defaultValue;
        return prefs.getOrDefault(key, defaultValue);
    }

    /**
     * Returns the player's current value for the given key, or empty if not set.
     */
    public static Optional<Boolean> get(UUID playerUUID, String key)
    {
        Map<String, Boolean> prefs = STORE.get(playerUUID);
        if (prefs == null) return Optional.empty();
        Boolean v = prefs.get(key);
        return Optional.ofNullable(v);
    }


    /**
     * Removes all stored preferences for the given player.
     * Call this from the player logout event to avoid memory leaks.
     */
    public static void clearPlayer(UUID playerUUID)
    {
        STORE.remove(playerUUID);
    }
}
