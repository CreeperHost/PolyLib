package net.creeperhost.polylib.accessibility;

/**
 * Callback that adds one or more widgets to the Accessibility Options screen via
 * {@link OptionsListTarget}.
 */
@FunctionalInterface
public interface OptionsWidgetProvider
{
    void addOptions(OptionsListTarget target);
}
