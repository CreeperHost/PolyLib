package net.creeperhost.polylib.chat.layout;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.creeperhost.polylib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saving and loading of ChatWindowLayouts to the local client disk.
 */
public class ChatLayoutManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Identifier.class, (JsonSerializer<Identifier>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Identifier.class, (JsonDeserializer<Identifier>) (json, typeOfT, context) -> Identifier.parse(json.getAsString()))
            .create();
            
    private final List<ChatWindowLayout> activeLayouts = new ArrayList<>();
    private File layoutFile;

    public ChatLayoutManager() {
    }

    public void init() {
        if (Minecraft.getInstance() != null) {
            File polyDir = new File(Minecraft.getInstance().gameDirectory, "config/polylib");
            polyDir.mkdirs();
            this.layoutFile = new File(polyDir, "chat_layouts.json");
            load();
        }
    }

    public List<ChatWindowLayout> getActiveLayouts() {
        return activeLayouts;
    }

    public ChatWindowLayout getOrCreateLayout(String layoutId) {
        for (ChatWindowLayout layout : activeLayouts) {
            if (layout.getLayoutId().equals(layoutId)) {
                return layout;
            }
        }
        ChatWindowLayout newLayout = new ChatWindowLayout(layoutId, 10, 10, 200, 150);
        activeLayouts.add(newLayout);
        save();
        return newLayout;
    }

    public void removeLayout(String layoutId) {
        activeLayouts.removeIf(l -> l.getLayoutId().equals(layoutId));
        save();
    }

    public void save() {
        if (layoutFile == null) return;
        try (FileWriter writer = new FileWriter(layoutFile)) {
            GSON.toJson(activeLayouts, writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save chat layouts", e);
        }
    }

    public void load() {
        if (layoutFile == null || !layoutFile.exists()) return;
        try (FileReader reader = new FileReader(layoutFile)) {
            Type listType = new TypeToken<ArrayList<ChatWindowLayout>>(){}.getType();
            List<ChatWindowLayout> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) {
                activeLayouts.clear();
                activeLayouts.addAll(loaded);
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to load chat layouts", e);
        }
    }
}
