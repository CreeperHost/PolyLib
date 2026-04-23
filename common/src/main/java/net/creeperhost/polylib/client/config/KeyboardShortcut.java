package net.creeperhost.polylib.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Describes an optional keyboard shortcut for a config panel entry.
 *
 * <h2>Variants</h2>
 * <ul>
 *   <li>{@link #unbound(KeyMapping.Category)} — shows in Controls but has no default key</li>
 *   <li>{@link #suggested(int, InputConstants.Type, KeyMapping.Category)} — has a suggested
 *       key but defaults to UNBOUND so it never conflicts</li>
 *   <li>{@link #bound(int, InputConstants.Type, KeyMapping.Category)} — bound by default;
 *       use sparingly</li>
 * </ul>
 *
 * <p>Create or reuse a {@link KeyMapping.Category} via
 * {@code KeyMapping.Category.register(Identifier.fromNamespaceAndPath(modId, "main"))}.</p>
 */
public record KeyboardShortcut(int defaultKey, InputConstants.Type inputType, KeyMapping.Category keyCategory, boolean suggestedOnly)
{
    /**
     * Unbound by default — appears in Controls with no assigned key.
     */
    public static KeyboardShortcut unbound(KeyMapping.Category category)
    {
        return new KeyboardShortcut(InputConstants.UNKNOWN.getValue(), InputConstants.Type.KEYSYM, category, false);
    }

    /**
     * Has a suggested default key, but ships as UNBOUND so it never conflicts with other mods.
     *
     * @param key      GLFW key constant, e.g. {@code GLFW.GLFW_KEY_F6}
     * @param type     Usually {@link InputConstants.Type#KEYSYM}
     * @param category The Controls category to group this keybind under
     */
    public static KeyboardShortcut suggested(int key, InputConstants.Type type, KeyMapping.Category category)
    {
        return new KeyboardShortcut(key, type, category, true);
    }

    /**
     * Bound by default. Prefer {@link #suggested} to avoid consuming user key space.
     */
    public static KeyboardShortcut bound(int key, InputConstants.Type type, KeyMapping.Category category)
    {
        return new KeyboardShortcut(key, type, category, false);
    }

    /**
     * Creates a {@link KeyMapping} for this shortcut with the given description key.
     * Suggested-only shortcuts are created with {@link InputConstants#UNKNOWN} as the default.
     *
     * @param descriptionKey Translation key for the keybind, e.g. {@code "key.mymod.config_panel"}
     */
    public KeyMapping toKeyMapping(String descriptionKey)
    {
        int actualDefault = suggestedOnly ? InputConstants.UNKNOWN.getValue() : defaultKey;
        return new KeyMapping(descriptionKey, inputType, actualDefault, keyCategory);
    }
}

