package net.creeperhost.polylib.accessibility;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;

import java.util.*;

/**
 * Central registry for accessibility option widgets that PolyLib injects into the vanilla
 * Accessibility Options screen.
 * <p>
 * Mods register providers during client init. PolyLib's event handlers call
 * {@link #inject(Screen)} on every screen init event.
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * // During client init:
 * AccessibilityOptionsRegistry.register("mymod.rescue", target -> {
 *     target.addCategory(Component.literal("My Mod"));
 *     target.addToggle(Component.literal("Fall Rescue"), prefs::isRescueOn, prefs::setRescueOn);
 * });
 * }</pre>
 */
public final class AccessibilityOptionsRegistry
{
    private AccessibilityOptionsRegistry() {}

    private record Entry(String key, AccessibilityPolicy policy, OptionsWidgetProvider provider) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();

    /**
     * Registers a widget provider with the default policy ({@link AccessibilityPolicy#PLAYER_OVERRIDES_SERVER}).
     *
     * @param key      Unique namespaced key, e.g. {@code "discrafthonored.rescue"}
     * @param provider Callback that adds widgets to the options list
     */
    public static void register(String key, OptionsWidgetProvider provider)
    {
        register(key, AccessibilityPolicy.PLAYER_OVERRIDES_SERVER, provider);
    }

    /**
     * Registers a widget provider with an explicit policy.
     *
     * @param key      Unique namespaced key
     * @param policy   Whether the player's value overrides server config
     * @param provider Callback that adds widgets to the options list
     */
    public static void register(String key, AccessibilityPolicy policy, OptionsWidgetProvider provider)
    {
        ENTRIES.add(new Entry(key, policy, provider));
    }

    /**
     * Builder-style overload — registers toggles/pairs/categories via a fluent
     * {@link AccessibilityRegistrationBuilder} and simultaneously captures default English
     * strings for lang datagen.
     *
     * @param key     Unique namespaced key, e.g. {@code "mymod.rescue"}
     * @param builder Consumer that configures the {@link AccessibilityRegistrationBuilder}
     */
    public static void register(String key, java.util.function.Consumer<AccessibilityRegistrationBuilder> builder)
    {
        register(key, AccessibilityPolicy.PLAYER_OVERRIDES_SERVER, builder);
    }

    /**
     * Builder-style registration with an explicit policy.
     *
     * @param key     Unique namespaced key
     * @param policy  Whether the player's value overrides server config
     * @param builder Consumer that configures the {@link AccessibilityRegistrationBuilder}
     */
    public static void register(String key, AccessibilityPolicy policy,
                                java.util.function.Consumer<AccessibilityRegistrationBuilder> builder)
    {
        AccessibilityRegistrationBuilder b = new AccessibilityRegistrationBuilder();
        builder.accept(b);
        ENTRIES.add(new Entry(key, policy, b));
    }

    /**
     * Called from the loader's screen init event. Injects all registered widgets when the
     * screen is {@link AccessibilityOptionsScreen}.
     */
    public static void inject(Screen screen)
    {
        if (!(screen instanceof AccessibilityOptionsScreen accessibilityScreen)) return;

        OptionsListTarget target = OptionsListTarget.from(accessibilityScreen);
        if (target == null) return;

        for (Entry entry : ENTRIES)
        {
            entry.provider().addOptions(target);
        }
    }

    /**
     * Returns the effective policy for the given key.
     */
    public static AccessibilityPolicy effectivePolicy(String key)
    {
        for (Entry e : ENTRIES)
        {
            if (e.key().equals(key)) return e.policy();
        }
        return AccessibilityPolicy.PLAYER_OVERRIDES_SERVER;
    }

    /**
     * Returns all registered keys whose effective policy is {@link AccessibilityPolicy#PLAYER_OVERRIDES_SERVER}.
     * Used by the server-side handler to decide which preference values to accept unconditionally.
     */
    public static Set<String> getServerSyncedKeys()
    {
        Set<String> keys = new LinkedHashSet<>();
        for (Entry e : ENTRIES)
        {
            if (e.policy() == AccessibilityPolicy.PLAYER_OVERRIDES_SERVER)
            {
                keys.add(e.key());
            }
        }
        return keys;
    }
}
