package net.creeperhost.polylib.data.lang;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Central store for PolyLib-managed default English translation strings.
 *
 * <p>Populated automatically by PolyLib registration APIs:
 * <ul>
 *   <li>{@code AccessibilityRegistrationBuilder} toggle/pair/category methods</li>
 *   <li>{@code ConfigPanelRegistry.register(modId, labelKey, defaultEnglish, ...)} overloads</li>
 *   <li>{@code PlayerClientSettingsRegistry.register(..., displayKey, displayEnglish)} overload</li>
 *   <li>{@code PolyRegistry.register(name, defaultEnglish, supplier)} overload</li>
 *   <li>Direct: {@link #contribute(String, String)}</li>
 * </ul>
 *
 * <p>Consumed by {@code PolyLibLangProvider} in PolyLib's NeoForge module during datagen.
 * Mod lang providers should extend {@code PolyLibLangProvider} instead of
 * {@code LanguageProvider} directly to receive
 * all contributions automatically.
 *
 * <p>Thread-safe. First registration wins — later contributions for an existing key are ignored,
 * allowing mods to override PolyLib defaults by contributing earlier.
 */
public final class PolyLangContributions
{
    private PolyLangContributions() {}

    private static final LinkedHashMap<String, String> ENTRIES = new LinkedHashMap<>();

    /**
     * Contributes a single translation key with its default English value.
     * If the key is already present, the existing value is kept (first-registration wins).
     *
     * @param key           Translation key, e.g. {@code "discrafthonored.accessibility.blink_rescue"}
     * @param defaultEnglish Human-readable English text, e.g. {@code "Save Me: Blink Rescue"}
     */
    public static synchronized void contribute(String key, String defaultEnglish)
    {
        ENTRIES.putIfAbsent(key, defaultEnglish);
    }

    /**
     * Contributes all entries from the given map.
     * For each key already present, the existing value is kept.
     *
     * @param entries Map of translationKey → defaultEnglish
     */
    public static synchronized void contributeAll(Map<String, String> entries)
    {
        entries.forEach(ENTRIES::putIfAbsent);
    }

    /**
     * Returns an unmodifiable snapshot of all contributed entries in insertion order.
     * Suitable for inspection, testing, or custom datagen.
     */
    public static synchronized Map<String, String> getAll()
    {
        return Collections.unmodifiableMap(new LinkedHashMap<>(ENTRIES));
    }

    /**
     * Iterates all contributed entries, passing each {@code (key, defaultEnglish)} pair to the
     * given consumer. Typically passed as {@code languageProvider::add} from a datagen provider.
     *
     * @param sink Receives each (translationKey, defaultEnglish) pair in insertion order
     */
    public static synchronized void drainTo(BiConsumer<String, String> sink)
    {
        ENTRIES.forEach(sink);
    }
}
