package net.creeperhost.polylib.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.PolylibCommon;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Central registry for mod config panel screens.
 *
 * <h2>Registration tiers</h2>
 * <ol>
 *   <li><b>Vanilla Screen</b> — {@link #register(String, ScreenFactory)}.
 *       Works on any loader. The dev provides a standard {@link Screen} factory.</li>
 *   <li><b>ModularGui</b> — {@link #registerModularGui(String, GuiProviderFactory)}.
 *       PolyLib wraps the {@link GuiProvider} in a {@link ModularGuiScreen} automatically.</li>
 *   <li><b>ModularGui Injection</b> — {@link #registerInjection(String, Predicate, Function)}.
 *       Overlays a ModularGui directly onto an existing vanilla screen via
 *       {@link ModularGuiInjector}. Use for advanced cases only.</li>
 * </ol>
 *
 * <h2>Auto-hooks</h2>
 * <ul>
 *   <li><b>NeoForge</b> — use {@code NeoForgeConfigHelper.register(...)} from the NeoForge
 *       module. This calls both this registry AND {@code IConfigScreenFactory} so the entry
 *       appears under the native "Mods → Config" button.</li>
 *   <li><b>Fabric / Mod Menu</b> — handled automatically via PolyLib's {@code ModMenuCompat}
 *       entrypoint when Mod Menu is present. Just call {@link #register} from the Fabric init.</li>
 *   <li><b>Button injection fallback</b> — PolyLib injects a button into
 *       {@code ConfigurationSectionScreen} and similar screens that the native hook doesn't cover.
 *       Keybinds are also ticked via {@link #tickKeybinds()}.</li>
 * </ul>
 *
 * <h2>PolyConfig json5 backing</h2>
 * The per-mod keybind enable/disable map is stored in {@code polylib.json5} and managed
 * through {@link PolylibCommon#configData}.
 */
public final class ConfigPanelRegistry
{
    private ConfigPanelRegistry() {}

    public sealed interface ConfigPanelEntry permits VanillaEntry, ModularEntry
    {
        String modId();

        /** Label shown on the injected config button. Defaults to a translatable key. */
        Component label();

        @Nullable
        KeyMapping keyMapping();

        Screen createScreen(Screen parent);
    }

    public record VanillaEntry(String modId, Component label, ScreenFactory factory, @Nullable KeyMapping keyMapping)
            implements ConfigPanelEntry
    {
        @Override
        public Screen createScreen(Screen parent)
        {
            return factory.create(parent);
        }
    }

    public record ModularEntry(String modId, Component label, GuiProviderFactory factory, @Nullable KeyMapping keyMapping)
            implements ConfigPanelEntry
    {
        @Override
        public Screen createScreen(Screen parent)
        {
            return new ModularGuiScreen(factory.create(parent), parent);
        }
    }

    private static final LinkedHashMap<String, ConfigPanelEntry> ENTRIES = new LinkedHashMap<>();

    public static void register(String modId, ScreenFactory factory)
    {
        register(modId, defaultLabel(modId), factory, null);
    }

    public static void register(String modId, ScreenFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        register(modId, defaultLabel(modId), factory, shortcut);
    }

    /** Register with a custom button label (e.g. {@code Component.translatable("mymod.config.appearance")}). */
    public static void register(String modId, Component label, ScreenFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        KeyMapping km = buildKeyMapping(modId, shortcut);
        ENTRIES.put(modId, new VanillaEntry(modId, label, factory, km));
    }

    /**
     * Register with a translation key and default English label.
     *
     * @param modId          Mod ID
     * @param labelKey       Translation key for the config button label
     * @param defaultEnglish Default English text shown on the button
     * @param factory        Screen factory
     * @param shortcut       Optional keybind shortcut (may be null)
     */
    public static void register(String modId, String labelKey, String defaultEnglish,
                                ScreenFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        register(modId, Component.translatable(labelKey), factory, shortcut);
    }


    public static void registerModularGui(String modId, GuiProviderFactory factory)
    {
        registerModularGui(modId, defaultLabel(modId), factory, null);
    }

    public static void registerModularGui(String modId, GuiProviderFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        registerModularGui(modId, defaultLabel(modId), factory, shortcut);
    }

    /** Register with a custom button label. */
    public static void registerModularGui(String modId, Component label, GuiProviderFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        KeyMapping km = buildKeyMapping(modId, shortcut);
        ENTRIES.put(modId, new ModularEntry(modId, label, factory, km));
    }

    /**
     * Register a ModularGui screen with a translation key and default English label.
     *
     * @param modId          Mod ID
     * @param labelKey       Translation key for the config button label
     * @param defaultEnglish Default English text shown on the button
     * @param factory        ModularGui provider factory
     * @param shortcut       Optional keybind shortcut (may be null)
     */
    public static void registerModularGui(String modId, String labelKey, String defaultEnglish,
                                          GuiProviderFactory factory, @Nullable KeyboardShortcut shortcut)
    {
        registerModularGui(modId, Component.translatable(labelKey), factory, shortcut);
    }

    /**
     * Registers a ModularGui overlay that is injected directly into a matching vanilla screen
     * rather than opening as a separate screen. Delegates to {@link ModularGuiInjector}.
     * Does NOT appear in Mod Menu or native NeoForge config screen list.
     */
    public static void registerInjection(String modId, Predicate<Screen> screenPredicate,
                                         Function<Screen, GuiProvider> guiFunction)
    {
        // ModularGuiInjector is common code — safe to call here
        ModularGuiInjector.registerInjection(screenPredicate, s -> guiFunction.apply(s));
    }


    public static Map<String, ConfigPanelEntry> getAll()
    {
        return Collections.unmodifiableMap(ENTRIES);
    }

    @Nullable
    public static ConfigPanelEntry get(String modId)
    {
        return ENTRIES.get(modId);
    }

    /**
     * Checks all registered keybinds and opens the config screen when one is consumed.
     * Only fires when no other screen is open.
     */
    public static void tickKeybinds()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        for (ConfigPanelEntry entry : ENTRIES.values())
        {
            KeyMapping km = entry.keyMapping();
            if (km == null) continue;
            if (!isKeybindEnabled(entry.modId())) continue;
            while (km.consumeClick())
            {
                mc.setScreen(entry.createScreen(null));
            }
        }
    }

    /**
     * Returns all registered {@link KeyMapping}s for loader key-registration events.
     */
    public static List<KeyMapping> getAllKeyMappings()
    {
        List<KeyMapping> list = new ArrayList<>();
        for (ConfigPanelEntry entry : ENTRIES.values())
        {
            if (entry.keyMapping() != null) list.add(entry.keyMapping());
        }
        return list;
    }


    private static Component defaultLabel(String modId)
    {
        return Component.translatable(modId + ".polylib.config_button");
    }

    @Nullable
    private static KeyMapping buildKeyMapping(String modId, @Nullable KeyboardShortcut shortcut)
    {
        if (shortcut == null) return null;

        // Check if the user has disabled this keybind in polylib.json5
        if (!isKeybindEnabled(modId)) return null;

        // Auto-add to PolyConfig so it appears in the file the first time
        if (PolylibCommon.configData != null
                && !PolylibCommon.configData.configPanelKeybinds.containsKey(modId))
        {
            PolylibCommon.configData.configPanelKeybinds.put(modId, true);
            if (PolylibCommon.configBuilder != null)
            {
                PolylibCommon.configBuilder.save(PolylibCommon.configData);
            }
        }

        return shortcut.toKeyMapping("key." + modId + ".config_panel");
    }

    private static boolean isKeybindEnabled(String modId)
    {
        if (PolylibCommon.configData == null) return true;
        return PolylibCommon.configData.configPanelKeybinds.getOrDefault(modId, true);
    }
}
