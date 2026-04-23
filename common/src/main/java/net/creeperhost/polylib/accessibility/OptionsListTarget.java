package net.creeperhost.polylib.accessibility;

import net.creeperhost.polylib.Constants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Wraps a reflected {@link OptionsList} and exposes a concise builder API for adding widgets
 * to the Accessibility Options screen.
 * <p>
 * Uses reflection to call the NeoForge-patched {@code addSmall(AbstractWidget, AbstractWidget)}
 * and {@code addBig(AbstractWidget)} overloads which are not present in the vanilla compile target.
 */
public final class OptionsListTarget
{
    private static Field optionsListField = null;
    private static boolean fieldReflectionAttempted = false;
    private static Method addSmallMethod = null;
    private static Method addBigMethod = null;
    private static boolean methodReflectionAttempted = false;

    private final OptionsList optionsList;

    OptionsListTarget(OptionsList optionsList)
    {
        this.optionsList = optionsList;
        ensureMethodsReflected();
    }


    /** Adds two small (half-width) widgets side by side. Right may be null for a single entry. */
    public void addSmall(AbstractWidget left, @Nullable AbstractWidget right)
    {
        if (addSmallMethod != null)
        {
            try
            {
                addSmallMethod.invoke(optionsList, left, right);
            }
            catch (Exception e)
            {
                Constants.LOG.debug("[PolyLib/AccessibilityOptions] addSmall reflection failed: {}", e.getMessage());
            }
        }
    }

    /** Adds a single full-width widget. */
    public void addBig(AbstractWidget widget)
    {
        if (addBigMethod != null)
        {
            try
            {
                addBigMethod.invoke(optionsList, widget);
            }
            catch (Exception e)
            {
                Constants.LOG.debug("[PolyLib/AccessibilityOptions] addBig reflection failed: {}", e.getMessage());
            }
        }
    }

    /** Adds a toggle (On/Off) button. */
    public CycleButton<Boolean> addToggle(Component label, BooleanSupplier getter, Consumer<Boolean> setter)
    {
        CycleButton<Boolean> btn = CycleButton.onOffBuilder(getter.getAsBoolean())
                .create(0, 0, 150, 20, label, (button, value) -> setter.accept(value));
        addSmall(btn, null);
        return btn;
    }

    public void addTogglePair(Component leftLabel, BooleanSupplier leftGetter, Consumer<Boolean> leftSetter,
                              Component rightLabel, BooleanSupplier rightGetter, Consumer<Boolean> rightSetter)
    {
        CycleButton<Boolean> left = CycleButton.onOffBuilder(leftGetter.getAsBoolean())
                .create(0, 0, 150, 20, leftLabel, (btn, val) -> leftSetter.accept(val));
        CycleButton<Boolean> right = CycleButton.onOffBuilder(rightGetter.getAsBoolean())
                .create(0, 0, 150, 20, rightLabel, (btn, val) -> rightSetter.accept(val));
        addSmall(left, right);
    }

    public void addCategory(Component title)
    {
        net.minecraft.client.gui.components.Button header =
                net.minecraft.client.gui.components.Button.builder(title, b -> {})
                        .size(310, 20)
                        .build();
        header.active = false;
        addBig(header);
    }


    /**
     * Reflects the {@link OptionsList} field from the given Accessibility Options screen.
     * Returns null (and logs once) if reflection fails.
     */
    @Nullable
    static OptionsListTarget from(net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen screen)
    {
        if (!fieldReflectionAttempted)
        {
            fieldReflectionAttempted = true;
            try
            {
                for (Field f : net.minecraft.client.gui.screens.options.OptionsSubScreen.class.getDeclaredFields())
                {
                    if (f.getType() == OptionsList.class)
                    {
                        f.setAccessible(true);
                        optionsListField = f;
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                Constants.LOG.warn("[PolyLib/AccessibilityOptions] Failed to reflect OptionsList field: {}", e.getMessage());
            }
        }

        if (optionsListField == null) return null;

        try
        {
            OptionsList list = (OptionsList) optionsListField.get(screen);
            return list != null ? new OptionsListTarget(list) : null;
        }
        catch (Exception e)
        {
            Constants.LOG.debug("[PolyLib/AccessibilityOptions] Failed to get OptionsList instance: {}", e.getMessage());
            return null;
        }
    }

    private static void ensureMethodsReflected()
    {
        if (methodReflectionAttempted) return;
        methodReflectionAttempted = true;
        try
        {
            // NeoForge patches OptionsList to accept AbstractWidget directly.
            // This overload does not exist in vanilla — reflection lets us call it
            // without a compile-time dependency on NeoForge's patched classes.
            addSmallMethod = OptionsList.class.getMethod("addSmall", AbstractWidget.class, AbstractWidget.class);
        }
        catch (NoSuchMethodException e)
        {
            Constants.LOG.debug("[PolyLib/AccessibilityOptions] addSmall(AbstractWidget, AbstractWidget) not found — running on vanilla/Fabric?");
        }
        try
        {
            addBigMethod = OptionsList.class.getMethod("addBig", AbstractWidget.class);
        }
        catch (NoSuchMethodException e)
        {
            Constants.LOG.debug("[PolyLib/AccessibilityOptions] addBig(AbstractWidget) not found — running on vanilla/Fabric?");
        }
    }
}
