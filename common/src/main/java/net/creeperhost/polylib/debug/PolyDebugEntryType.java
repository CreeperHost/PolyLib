package net.creeperhost.polylib.debug;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Typed registration token for a custom F3 debug screen entry.
 * <p>
 * Obtain via {@link PolyDebugRegistry#register} and store as a
 * {@code public static final} constant in your mod class.
 *
 * <h3>Toggle / profile behaviour</h3>
 * <ul>
 *   <li>{@link #defaultEnabled} — whether the toggle button is ON by default
 *       when no saved preference exists yet.  MC persists the toggle across
 *       sessions in the vanilla {@code options.txt} ({@code debugScreen.*}).</li>
 *   <li>{@link #inDefaultProfile} — automatically enable this entry when
 *       the player activates the "Default" F3 profile (F3 + N in vanilla).</li>
 *   <li>{@link #inPerformanceProfile} — automatically enable this entry when
 *       the "Performance" F3 profile is active.</li>
 * </ul>
 */
public final class PolyDebugEntryType
{
    private final Identifier      id;
    private final PolyDebugEntry  entry;
    private final boolean         defaultEnabled;
    private final boolean         inDefaultProfile;
    private final boolean         inPerformanceProfile;
    @Nullable private final String displayNameKey;

    PolyDebugEntryType(Identifier     id,
                       PolyDebugEntry entry,
                       boolean        defaultEnabled,
                       boolean        inDefaultProfile,
                       boolean        inPerformanceProfile,
                       @Nullable String displayNameKey)
    {
        this.id                  = id;
        this.entry               = entry;
        this.defaultEnabled      = defaultEnabled;
        this.inDefaultProfile    = inDefaultProfile;
        this.inPerformanceProfile = inPerformanceProfile;
        this.displayNameKey      = displayNameKey;
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    /** Namespaced registration ID, e.g. {@code "mymod:my_entry"}. */
    public Identifier id()                  { return id; }

    /** The entry callback implementation. */
    public PolyDebugEntry entry()           { return entry; }

    /**
     * Whether the F3 toggle for this entry starts in the ON position when no
     * saved state exists.  Vanilla persists the toggle in {@code options.txt}.
     */
    public boolean defaultEnabled()         { return defaultEnabled; }

    /** Whether this entry is included in the "Default" F3 debug profile. */
    public boolean inDefaultProfile()       { return inDefaultProfile; }

    /** Whether this entry is included in the "Performance" F3 debug profile. */
    public boolean inPerformanceProfile()   { return inPerformanceProfile; }

    /**
     * Optional translation key for the toggle label shown in the F3 options
     * menu.  May be {@code null} if no name was registered, in which case
     * the raw {@link #id()} string will typically be used as the label.
     */
    @Nullable
    public String displayNameKey()          { return displayNameKey; }

    @Override
    public String toString()
    {
        return "PolyDebugEntryType[" + id + "]";
    }
}
