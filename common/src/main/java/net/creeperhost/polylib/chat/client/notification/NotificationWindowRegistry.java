package net.creeperhost.polylib.chat.client.notification;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Registry for notification windows. Mods register named windows and push entries to them.
 */
public class NotificationWindowRegistry {

    private static final Map<Identifier, WindowState> windows = new LinkedHashMap<>();
    private static final List<NotificationListener> listeners = new ArrayList<>();

    /**
     * Register a named notification window.
     */
    public static void register(Identifier windowId, Component title, NotificationWindowMode mode) {
        windows.put(windowId, new WindowState(windowId, title, mode));
    }

    /**
     * Unregister a notification window.
     */
    public static void unregister(Identifier windowId) {
        windows.remove(windowId);
    }

    /**
     * Send a notification entry to a specific window.
     */
    public static void send(Identifier windowId, NotificationEntry entry) {
        WindowState state = windows.get(windowId);
        if (state == null) return;
        state.entries.add(entry);
        if (state.entries.size() > 500) {
            state.entries.remove(0);
        }
        List<NotificationListener> snapshot = new ArrayList<>(listeners);
        for (NotificationListener l : snapshot) {
            l.onNotification(windowId, entry);
        }
    }

    /**
     * Get all entries for a window, oldest first.
     */
    public static List<NotificationEntry> getEntries(Identifier windowId) {
        WindowState state = windows.get(windowId);
        return state != null ? Collections.unmodifiableList(state.entries) : List.of();
    }

    /**
     * Get the unread count for a window.
     */
    public static int getUnreadCount(Identifier windowId) {
        WindowState state = windows.get(windowId);
        if (state == null) return 0;
        return (int) state.entries.stream().filter(e -> !e.read()).count();
    }

    /**
     * Mark a specific entry as read.
     */
    public static void markRead(Identifier windowId, UUID entryId) {
        WindowState state = windows.get(windowId);
        if (state == null) return;
        state.entries.replaceAll(e -> e.entryId().equals(entryId) ? e.markRead() : e);
    }

    /**
     * Mark all entries in a window as read.
     */
    public static void markAllRead(Identifier windowId) {
        WindowState state = windows.get(windowId);
        if (state == null) return;
        state.entries.replaceAll(NotificationEntry::markRead);
    }

    /**
     * Clear all entries from a window.
     */
    public static void clearAll(Identifier windowId) {
        WindowState state = windows.get(windowId);
        if (state != null) state.entries.clear();
    }

    public static boolean isRegistered(Identifier windowId) {
        return windows.containsKey(windowId);
    }

    public static WindowState getWindowState(Identifier windowId) {
        return windows.get(windowId);
    }

    public static Collection<WindowState> getAllWindows() {
        return Collections.unmodifiableCollection(windows.values());
    }

    public static void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Mutable state holder for a registered window.
     */
    public static class WindowState {
        private final Identifier windowId;
        private final Component title;
        private NotificationWindowMode mode;
        final List<NotificationEntry> entries = new ArrayList<>();

        public WindowState(Identifier windowId, Component title, NotificationWindowMode mode) {
            this.windowId = windowId;
            this.title = title;
            this.mode = mode;
        }

        public Identifier getWindowId() { return windowId; }
        public Component getTitle() { return title; }
        public NotificationWindowMode getMode() { return mode; }
        public void setMode(NotificationWindowMode mode) { this.mode = mode; }
        public List<NotificationEntry> getEntries() { return Collections.unmodifiableList(entries); }
    }

    @FunctionalInterface
    public interface NotificationListener {
        void onNotification(Identifier windowId, NotificationEntry entry);
    }
}
