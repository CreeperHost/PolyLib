package net.creeperhost.polylib;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.creeperhost.polylib.client.config.ConfigPanelRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mod Menu entrypoint. Loaded only when Mod Menu is present (declared as a {@code suggests}
 * dependency, not a hard {@code depends}).
 * <p>
 * Reads from {@link ConfigPanelRegistry} — any mod that registered its config panel via
 * PolyLib automatically appears in Mod Menu without any additional work.
 */
public class ModMenuCompat implements ModMenuApi
{
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories()
    {
        Map<String, ConfigScreenFactory<?>> out = new LinkedHashMap<>();
        ConfigPanelRegistry.getAll().forEach((modId, entry) ->
                out.put(modId, parent -> entry.createScreen(parent)));
        return out;
    }
}
