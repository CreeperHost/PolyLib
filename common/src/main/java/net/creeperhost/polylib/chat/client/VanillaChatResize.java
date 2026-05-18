package net.creeperhost.polylib.chat.client;

import net.creeperhost.polylib.chat.layout.ChatLayoutManager;

/**
 * Opt-in API for making the vanilla chat area resizable.
 * PolyLib does NOT register this by default. Mods call {@link #enable()} to opt in.
 *
 * When enabled, the vanilla chat history region gains drag handles on its top and right
 * edges, allowing the player to resize the chat area. The resized dimensions are persisted
 * in ChatLayoutManager under a special layout ID.
 */
public class VanillaChatResize {

    public static final String LAYOUT_ID = "polylib:vanilla_chat";

    private static boolean enabled = false;
    private static int customWidth = -1;  // -1 = use vanilla default
    private static int customHeight = -1; // -1 = use vanilla default

    /**
     * Enable resizable vanilla chat. Call during client init.
     */
    public static void enable() {
        enabled = true;
    }

    /**
     * Disable resizable vanilla chat. Reverts to vanilla dimensions.
     */
    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getCustomWidth() { return customWidth; }
    public static void setCustomWidth(int width) { customWidth = width; }

    public static int getCustomHeight() { return customHeight; }
    public static void setCustomHeight(int height) { customHeight = height; }

    public static void save(ChatLayoutManager manager) {
        var layout = manager.getOrCreateLayout(LAYOUT_ID);
        layout.setWidth(customWidth);
        layout.setHeight(customHeight);
        manager.save();
    }

    public static void load(ChatLayoutManager manager) {
        var layout = manager.getOrCreateLayout(LAYOUT_ID);
        if (layout.getWidth() > 0) customWidth = layout.getWidth();
        if (layout.getHeight() > 0) customHeight = layout.getHeight();
    }
}
