package net.creeperhost.polylib.player.serverdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-player in-memory store for server-authoritative player data values.
 * Internal to PolyLib — not part of the public API.
 * Mirrors {@code PlayerClientSettingsStore} but keyed on {@link PlayerServerDataType}.
 */
public final class PlayerServerDataStore
{
    private final Map<PlayerServerDataType<?>, Object> cache = new HashMap<>();
    private final Set<PlayerServerDataType<?>> dirty = new HashSet<>();

    /** Get the current value, or the type's default if not yet loaded. */
    @SuppressWarnings("unchecked")
    public <T> T get(PlayerServerDataType<T> type)
    {
        return (T) cache.computeIfAbsent(type, t -> t.defaultFactory().get());
    }

    /** Set a value and mark it dirty for persistence. */
    public <T> void set(PlayerServerDataType<T> type, T value)
    {
        cache.put(type, value);
        dirty.add(type);
    }

    /** Set a value without marking dirty (used when loading persisted data). */
    public <T> void load(PlayerServerDataType<T> type, T value)
    {
        cache.put(type, value);
    }

    /** Returns all types that have been modified since last save. */
    public Set<PlayerServerDataType<?>> getDirty()
    {
        return dirty;
    }

    /** Clear the dirty set after a successful save. */
    public void clearDirty()
    {
        dirty.clear();
    }

    /** Whether any type has a loaded value. */
    public boolean has(PlayerServerDataType<?> type)
    {
        return cache.containsKey(type);
    }
}
