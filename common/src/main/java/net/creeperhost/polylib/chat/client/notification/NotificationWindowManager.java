package net.creeperhost.polylib.chat.client.notification;

import net.creeperhost.polylib.chat.client.ChatWindowManager;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages FloatingNotificationWindow instances for FLOATING-mode notification windows.
 */
public class NotificationWindowManager {

    private final ChatWindowManager windowManager;
    private final Map<Identifier, FloatingNotificationWindow> activeWindows = new HashMap<>();

    public NotificationWindowManager(ChatWindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * Open a floating notification window if one isn't already open.
     */
    public FloatingNotificationWindow openWindow(GuiParent<?> parent, Identifier windowId) {
        if (activeWindows.containsKey(windowId)) {
            return activeWindows.get(windowId);
        }
        FloatingNotificationWindow window = new FloatingNotificationWindow(parent, windowId);
        window.setWindowManager(windowManager);
        window.constrain(GeoParam.LEFT, Constraint.literal(10))
              .constrain(GeoParam.TOP, Constraint.literal(10))
              .constrain(GeoParam.WIDTH, Constraint.literal(260))
              .constrain(GeoParam.HEIGHT, Constraint.literal(160));
        windowManager.addWindow(window);
        activeWindows.put(windowId, window);
        return window;
    }

    /**
     * Close a floating notification window.
     */
    public void closeWindow(Identifier windowId) {
        FloatingNotificationWindow window = activeWindows.remove(windowId);
        if (window != null) {
            window.dispose();
            windowManager.removeWindow(window);
        }
    }

    public boolean isOpen(Identifier windowId) {
        return activeWindows.containsKey(windowId);
    }

    /**
     * Open all FLOATING-mode registered windows.
     */
    public void restoreFloatingWindows(GuiParent<?> parent) {
        for (NotificationWindowRegistry.WindowState state : NotificationWindowRegistry.getAllWindows()) {
            if (state.getMode() == NotificationWindowMode.FLOATING) {
                openWindow(parent, state.getWindowId());
            }
        }
    }
}
