package net.creeperhost.polylib.debug;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for {@link PolyDebugEntryType} tokens.
 * <p>
 * Registration <strong>must</strong> happen before the debug screen is first
 * opened — typically in your mod's client initialiser or static init.
 * On NeoForge this registry is consumed by {@link net.creeperhost.polylib.debug.neoforge.NeoForgeDebugBridge}
 * during the {@code RegisterDebugEntriesEvent} (mod bus).
 * On Fabric it is consumed by the {@code FabricDebugEntriesMixin} injected
 * into {@code DebugScreenEntries}.
 *
 * <h3>Minimal usage example</h3>
 * <pre>{@code
 * // In your mod client init:
 * public static final PolyDebugEntryType MY_ENTRY = PolyDebugRegistry.register(
 *     Identifier.fromNamespaceAndPath("mymod", "my_info"),
 *     displayer -> displayer.addLine("MyMod: " + getSomeValue()),
 *     true,   // ON by default
 *     true,   // included in the Default profile
 *     false,  // not in the Performance profile
 *     "debug.mymod.my_info",   // i18n key
 *     "My Info"                // English fallback for datagen
 * );
 * }</pre>
 */
public final class PolyDebugRegistry
{
    private static final Map<Identifier, PolyDebugEntryType> BY_ID = new LinkedHashMap<>();

    private PolyDebugRegistry() {}

    // ── Registration ──────────────────────────────────────────────────────────

    /**
     * Register a new debug screen entry (minimal overload — no display name,
     * enabled by default, included in the Default profile only).
     *
     * @param id    Namespaced ID, e.g. {@code "mymod:my_entry"}
     * @param entry Callback that renders the entry's lines
     * @return Typed token — store as {@code public static final}
     */
    public static PolyDebugEntryType register(Identifier id, PolyDebugEntry entry)
    {
        return register(id, entry, true, true, false, null, null);
    }

    /**
     * Register with explicit profile and default-enabled control.
     *
     * @param id                  Namespaced ID
     * @param entry               Callback that renders the entry's lines
     * @param defaultEnabled      Initial toggle state when no saved preference exists
     * @param inDefaultProfile    Include in the "Default" F3 debug profile
     * @param inPerformanceProfile Include in the "Performance" F3 debug profile
     * @return Typed token — store as {@code public static final}
     */
    public static PolyDebugEntryType register(Identifier id,
                                              PolyDebugEntry entry,
                                              boolean defaultEnabled,
                                              boolean inDefaultProfile,
                                              boolean inPerformanceProfile)
    {
        return register(id, entry, defaultEnabled, inDefaultProfile, inPerformanceProfile, null, null);
    }

    /**
     * Full registration overload with optional display name for lang datagen.
     * <p>
     * If both {@code displayNameKey} and {@code displayNameEnglish} are
     * non-null, the pair is contributed to {@link PolyLangContributions} so
     * downstream lang datagen providers can automatically include it.
     *
     * @param id                  Namespaced ID, e.g. {@code "mymod:my_entry"}
     * @param entry               Callback that renders the entry's lines
     * @param defaultEnabled      Initial toggle state when no saved preference exists
     * @param inDefaultProfile    Include in the "Default" F3 debug profile
     * @param inPerformanceProfile Include in the "Performance" F3 debug profile
     * @param displayNameKey      i18n key for the toggle label, or {@code null}
     * @param displayNameEnglish  English fallback used during lang datagen, or {@code null}
     * @return Typed token — store as {@code public static final}
     */
    public static PolyDebugEntryType register(Identifier     id,
                                              PolyDebugEntry entry,
                                              boolean        defaultEnabled,
                                              boolean        inDefaultProfile,
                                              boolean        inPerformanceProfile,
                                              @Nullable String displayNameKey,
                                              @Nullable String displayNameEnglish)
    {
        if (BY_ID.containsKey(id))
            throw new IllegalStateException("PolyDebugEntryType already registered: " + id);

        if (displayNameKey != null && displayNameEnglish != null)
            PolyLangContributions.contribute(displayNameKey, displayNameEnglish);

        PolyDebugEntryType type = new PolyDebugEntryType(
                id, entry, defaultEnabled, inDefaultProfile, inPerformanceProfile, displayNameKey);
        BY_ID.put(id, type);
        return type;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Returns all registered types in insertion order.
     * Used internally by NeoForge/Fabric bridges.
     */
    public static Collection<PolyDebugEntryType> getAll()
    {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    /** Lookup by namespaced ID. */
    public static Optional<PolyDebugEntryType> byId(Identifier id)
    {
        return Optional.ofNullable(BY_ID.get(id));
    }

    /** Returns {@code true} if the given ID has been registered. */
    public static boolean isRegistered(Identifier id)
    {
        return BY_ID.containsKey(id);
    }
}
