package net.creeperhost.polylib.datagen;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Abstract {@link LanguageProvider} that automatically includes every translation entry
 * contributed to PolyLib's lang datagen system via any PolyLib registration API.
 *
 * <h3>Usage</h3>
 * Extend this class in your mod's NeoForge datagen module instead of extending
 * {@link LanguageProvider} directly:
 * <pre>{@code
 * public class ModLangProvider extends PolyLibLangProvider {
 *     public ModLangProvider(PackOutput output) {
 *         super(output, "mymod");
 *     }
 *
 *     @Override
 *     protected void addModTranslations() {
 *         // Only entries NOT managed by PolyLib registration APIs.
 *         // Items, blocks, and other objects registered with
 *         //   PolyRegistry.register(name, defaultEnglish, supplier)
 *         // and accessibility options registered with
 *         //   AccessibilityOptionsRegistry.register(key, builder -> ...)
 *         // are included AUTOMATICALLY — no add() calls needed here.
 *         add("message.mymod.some_event", "Something happened!");
 *     }
 * }
 * }</pre>
 *
 * <h3>What is auto-included</h3>
 * Any key+English pair contributed to {@link PolyLangContributions} via:
 * <ul>
 *   <li>{@code AccessibilityOptionsRegistry.register(key, builder → ...)} —
 *       all {@code .toggle()}, {@code .togglePair()}, and {@code .category()} labels</li>
 *   <li>{@code ConfigPanelRegistry.register(modId, labelKey, defaultEnglish, factory, shortcut)}</li>
 *   <li>{@code ConfigPanelRegistry.registerModularGui(modId, labelKey, defaultEnglish, factory, shortcut)}</li>
 *   <li>{@code PlayerClientSettingsRegistry.register(..., displayNameKey, displayNameEnglish)}</li>
 *   <li>{@code PolyRegistry.register(name, defaultEnglish, supplier)} — block/item names</li>
 *   <li>{@code PolyLangContributions.contribute(key, english)} — direct contributions</li>
 * </ul>
 *
 * <h3>Locale support</h3>
 * Use {@link #PolyLibLangProvider(PackOutput, String, String)} to generate a locale other
 * than {@code en_us}.
 */
public abstract class PolyLibLangProvider extends LanguageProvider
{
    protected PolyLibLangProvider(PackOutput output, String modId)
    {
        super(output, modId, "en_us");
    }

    protected PolyLibLangProvider(PackOutput output, String modId, String locale)
    {
        super(output, modId, locale);
    }

    /**
     * Final — do not override. Drains all PolyLib-tracked contributions first, then
     * calls {@link #addModTranslations()} for mod-specific manual entries.
     *
     * <p>If a key is contributed by both PolyLib and {@code addModTranslations()}, the
     * manual call in {@code addModTranslations()} will overwrite the PolyLib default,
     * giving mods full control to customise any auto-contributed string.
     */
    @Override
    protected final void addTranslations()
    {
        // All PolyLib-managed contributions (accessibility labels, config panel buttons,
        // settings type display names, registry object names, etc.)
        PolyLangContributions.drainTo(this::add);

        // Mod-specific manual entries — anything not managed by PolyLib APIs.
        // Calling add() for a key already contributed above is fine — it will overwrite
        // the PolyLib default with the mod's explicit value.
        addModTranslations();
    }

    /**
     * Override to add translation entries NOT automatically contributed via PolyLib APIs.
     *
     * <p>You do NOT need to add entries for:
     * <ul>
     *   <li>Items/blocks registered via {@code PolyRegistry.register(name, english, supplier)}</li>
     *   <li>Accessibility toggle labels registered via {@code AccessibilityOptionsRegistry.register(key, builder)}</li>
     *   <li>Config panel button labels registered via the string-key overloads of {@code ConfigPanelRegistry}</li>
     *   <li>Settings type display names registered via the display-name overloads of {@code PlayerClientSettingsRegistry}</li>
     * </ul>
     *
     * <p>You DO still need to add entries for:
     * <ul>
     *   <li>Items/blocks registered with the old {@code register(name, supplier)} overload (no English provided)</li>
     *   <li>Chat messages, tooltips, subtitles, and any other keys not tied to a PolyLib registration</li>
     * </ul>
     */
    protected abstract void addModTranslations();
}
