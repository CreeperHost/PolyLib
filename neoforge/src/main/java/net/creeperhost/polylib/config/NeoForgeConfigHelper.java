package net.creeperhost.polylib.config;

import net.creeperhost.polylib.client.config.ConfigPanelRegistry;
import net.creeperhost.polylib.client.config.GuiProviderFactory;
import net.creeperhost.polylib.client.config.KeyboardShortcut;
import net.creeperhost.polylib.client.config.ScreenFactory;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-specific helper for registering a mod config screen with PolyLib.
 *
 * <p>A single call to one of these methods registers the screen with both:
 * <ol>
 *   <li>{@link ConfigPanelRegistry} — powers PolyLib's keybind, button injection, and Fabric Mod Menu</li>
 *   <li>NeoForge's {@link IConfigScreenFactory} extension point — makes the screen appear under
 *       the native "Mods → Config" button without any extra boilerplate</li>
 * </ol>
 *
 * <h2>Usage (NeoForge mod constructor)</h2>
 * <pre>{@code
 * public MyMod(ModContainer container, IEventBus bus) {
 *     // Vanilla Screen variant:
 *     NeoForgeConfigHelper.register(container, parent -> new MyConfigScreen(container, parent));
 *
 *     // ModularGui variant:
 *     NeoForgeConfigHelper.registerModularGui(container, parent -> new MyConfigLayout(container, parent));
 * }
 * }</pre>
 */
public final class NeoForgeConfigHelper
{
    private NeoForgeConfigHelper() {}

    public static void register(ModContainer container, ScreenFactory factory)
    {
        register(container, factory, null);
    }

    public static void register(ModContainer container, ScreenFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        ConfigPanelRegistry.register(container.getModId(), factory, shortcut);
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> factory.create(parent));
    }

    public static void registerModularGui(ModContainer container, GuiProviderFactory factory)
    {
        registerModularGui(container, factory, null);
    }

    public static void registerModularGui(ModContainer container, GuiProviderFactory factory,
                                          @Nullable KeyboardShortcut shortcut)
    {
        ConfigPanelRegistry.registerModularGui(container.getModId(), factory, shortcut);
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> new ModularGuiScreen(factory.create(parent), parent));
    }
}
