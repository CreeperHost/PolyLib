package net.creeperhost.polylib.player.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-player in-memory store for PlayerClientSettings values.
 * Internal to PolyLib — not part of public API.
 * Provides zero-deserialization reads via a typed object cache.
 */
public final class PlayerClientSettingsStore
{
    private final Map<PlayerClientSettingsType<?>, Object> cache = new HashMap<>();
    private final Set<PlayerClientSettingsType<?>> dirty = new HashSet<>();

    /** Get the current value, or the type's default if not yet loaded. */
    @SuppressWarnings("unchecked")
    public <T> T get(PlayerClientSettingsType<T> type)
    {
        return (T) cache.computeIfAbsent(type, t -> t.defaultFactory().get());
    }

    /** Set a value and mark it dirty for persistence. */
    public <T> void set(PlayerClientSettingsType<T> type, T value)
    {
        cache.put(type, value);
        dirty.add(type);
    }

    /** Set a value without marking dirty (used when loading persisted data). */
    public <T> void load(PlayerClientSettingsType<T> type, T value)
    {
        cache.put(type, value);
    }

    /** Returns all types that have been modified since last save. */
    public Set<PlayerClientSettingsType<?>> getDirty()
    {
        return dirty;
    }

    /** Clear the dirty set after a successful save. */
    public void clearDirty()
    {
        dirty.clear();
    }

    /** Whether any type has a loaded value (i.e. data was loaded from persistence). */
    public boolean has(PlayerClientSettingsType<?> type)
    {
        return cache.containsKey(type);
    }
}
