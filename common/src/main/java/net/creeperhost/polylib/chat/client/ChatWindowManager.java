package net.creeperhost.polylib.chat.client;

import net.creeperhost.polylib.chat.layout.ChatWindowLayout;
import net.creeperhost.polylib.client.modulargui.elements.GuiWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages multiple GuiWindows, handles Z-indexing, focus, snapping, and tiling.
 */
public class ChatWindowManager {
    private static final int SNAP_DISTANCE = 10;
    
    private final List<GuiWindow> managedWindows = new ArrayList<>();

    public ChatWindowManager() {
    }

    public void addWindow(GuiWindow window) {
        managedWindows.add(window);
        
        // Add hooking for move completion to do snapping
        window.setOnMovedCallback(() -> handleWindowMoved(window));
    }

    public void removeWindow(GuiWindow window) {
        managedWindows.remove(window);
    }

    public void bringToFront(GuiWindow window) {
        if (managedWindows.remove(window)) {
            managedWindows.add(window);
        }
    }

    private void handleWindowMoved(GuiWindow movedWindow) {
        // Simple edge-snapping logic
        for (GuiWindow other : managedWindows) {
            if (other == movedWindow) continue;

            // Snap left edge to right edge
            if (Math.abs(movedWindow.xMin - other.xMax) < SNAP_DISTANCE) {
                // If y overlap exists
                if (movedWindow.yMax > other.yMin && movedWindow.yMin < other.yMax) {
                    movedWindow.xMin = other.xMax;
                    movedWindow.xMax = movedWindow.xMin + movedWindow.getMinSize().width(); // or preserve width
                }
            }
            
            // Further snapping logic can be expanded here
        }
    }
}
