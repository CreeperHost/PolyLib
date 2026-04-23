package net.creeperhost.polylib.accessibility;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;

/**
 * Fluent builder for registering accessibility options while simultaneously contributing
 * their default English translations to PolyLib's lang datagen system.
 *
 * <p>Used with the builder-style {@link AccessibilityOptionsRegistry#register} overloads:
 * <pre>{@code
 * AccessibilityOptionsRegistry.register("mymod.rescue", builder -> builder
 *     .togglePair(
 *         "mymod.accessibility.rescue_blink",    "Save Me: Blink",
 *         () -> prefs.blinkRescueOn, v -> prefs.blinkRescueOn = v,
 *         "mymod.accessibility.rescue_farreach", "Save Me: Far Reach",
 *         () -> prefs.farReachOn, v -> prefs.farReachOn = v
 *     )
 *     .toggle(
 *         "mymod.accessibility.void_grave", "Void Grave",
 *         () -> prefs.voidGraveOn, v -> prefs.voidGraveOn = v
 *     )
 * );
 * }</pre>
 *
 * <p>Each method contributes the given key+English pair to {@link PolyLangContributions}
 * and stores the corresponding widget action. The builder itself implements
 * {@link OptionsWidgetProvider} and is stored as the entry's provider.
 */
public final class AccessibilityRegistrationBuilder implements OptionsWidgetProvider
{
    private final List<Consumer<OptionsListTarget>> actions = new ArrayList<>();

    AccessibilityRegistrationBuilder() {}

    /** Adds a single toggle button. Contributes {@code key → defaultEnglish} to lang datagen. */
    public AccessibilityRegistrationBuilder toggle(String key, String defaultEnglish,
                                                   BooleanSupplier getter, Consumer<Boolean> setter)
    {
        PolyLangContributions.contribute(key, defaultEnglish);
        Component label = Component.translatable(key);
        actions.add(target -> target.addToggle(label, getter, setter));
        return this;
    }

    /** Adds two toggle buttons side by side. Contributes both keys to lang datagen. */
    public AccessibilityRegistrationBuilder togglePair(
            String leftKey,  String leftEnglish,  BooleanSupplier leftGetter,  Consumer<Boolean> leftSetter,
            String rightKey, String rightEnglish, BooleanSupplier rightGetter, Consumer<Boolean> rightSetter)
    {
        PolyLangContributions.contribute(leftKey,  leftEnglish);
        PolyLangContributions.contribute(rightKey, rightEnglish);
        Component leftLabel  = Component.translatable(leftKey);
        Component rightLabel = Component.translatable(rightKey);
        actions.add(target -> target.addTogglePair(leftLabel, leftGetter, leftSetter,
                                                   rightLabel, rightGetter, rightSetter));
        return this;
    }

    /** Adds a non-interactive section header. Contributes {@code key → defaultEnglish} to lang datagen. */
    public AccessibilityRegistrationBuilder category(String key, String defaultEnglish)
    {
        PolyLangContributions.contribute(key, defaultEnglish);
        Component title = Component.translatable(key);
        actions.add(target -> target.addCategory(title));
        return this;
    }

    @Override
    public void addOptions(OptionsListTarget target)
    {
        for (Consumer<OptionsListTarget> action : actions)
        {
            action.accept(target);
        }
    }
}
