package net.creeperhost.polylib.chat.client.notification;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Convenience API for the built-in shared PolyLib notification window.
 * All mods sending to {@link #send(NotificationEntry)} share one consolidated panel.
 */
public class PolyNotifications {

    public static final Identifier SHARED_WINDOW_ID =
            Identifier.fromNamespaceAndPath("polylib", "notifications");

    private static boolean initialized = false;

    /**
     * Ensure the shared window is registered. Called lazily on first send,
     * or explicitly during client init.
     */
    public static void init() {
        if (!initialized) {
            NotificationWindowRegistry.register(
                SHARED_WINDOW_ID,
                Component.literal("Notifications"),
                NotificationWindowMode.FLOATING
            );
            initialized = true;
        }
    }

    /**
     * Send a notification to the shared PolyLib notification window.
     */
    public static void send(NotificationEntry entry) {
        init();
        NotificationWindowRegistry.send(SHARED_WINDOW_ID, entry);
    }

    /**
     * Get the unread count on the shared window.
     */
    public static int getUnreadCount() {
        return NotificationWindowRegistry.getUnreadCount(SHARED_WINDOW_ID);
    }

    /**
     * Mark all shared notifications as read.
     */
    public static void markAllRead() {
        NotificationWindowRegistry.markAllRead(SHARED_WINDOW_ID);
    }

    /**
     * Clear all shared notifications.
     */
    public static void clearAll() {
        NotificationWindowRegistry.clearAll(SHARED_WINDOW_ID);
    }

    private PolyNotifications() {}
}
