package net.creeperhost.polylib.client.config;

import net.minecraft.client.gui.screens.Screen;

/**
 * Factory that creates a config {@link Screen} for a given parent screen.
 * Used by {@link ConfigPanelRegistry} to open a mod's config panel.
 */
@FunctionalInterface
public interface ScreenFactory
{
    Screen create(Screen parent);
}
