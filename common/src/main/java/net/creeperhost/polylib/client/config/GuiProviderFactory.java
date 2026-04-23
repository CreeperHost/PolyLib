package net.creeperhost.polylib.client.config;

import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.minecraft.client.gui.screens.Screen;

/**
 * Factory that creates a PolyLib {@link GuiProvider} for a given parent screen.
 * PolyLib wraps this in {@link net.creeperhost.polylib.client.modulargui.ModularGuiScreen} automatically.
 */
@FunctionalInterface
public interface GuiProviderFactory
{
    GuiProvider create(Screen parent);
}
