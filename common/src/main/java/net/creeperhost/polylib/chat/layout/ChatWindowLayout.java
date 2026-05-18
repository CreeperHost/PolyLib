package net.creeperhost.polylib.chat.layout;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the on-screen layout properties of a floating chat window.
 * This state is persisted to disk.
 */
public class ChatWindowLayout {
    private final String layoutId; // Could be a UUID or a custom string identifier for the window grouping
    
    // Position and dimensions (normalized or absolute screen coordinates)
    private int x;
    private int y;
    private int width;
    private int height;
    
    private boolean isPinned;
    private boolean isHidden;
    
    private DisplayMode displayMode = DisplayMode.FLOATING;
    private boolean minimized = false;
    private SnapCorner snapCorner = null;  // null = free position

    // Which channels are docked in this window (for tabs)
    private final List<Identifier> tabbedChannels = new ArrayList<>();
    // Which tab is currently active/visible
    private Identifier activeTab;

    public ChatWindowLayout(String layoutId, int x, int y, int width, int height) {
        this.layoutId = layoutId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getLayoutId() {
        return layoutId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public DisplayMode getDisplayMode() { return displayMode; }
    public void setDisplayMode(DisplayMode mode) { this.displayMode = mode; }

    public boolean isMinimized() { return minimized; }
    public void setMinimized(boolean minimized) { this.minimized = minimized; }

    public SnapCorner getSnapCorner() { return snapCorner; }
    public void setSnapCorner(SnapCorner snapCorner) { this.snapCorner = snapCorner; }

    public List<Identifier> getTabbedChannels() {
        return tabbedChannels;
    }

    public void addTab(Identifier channelId) {
        if (!tabbedChannels.contains(channelId)) {
            tabbedChannels.add(channelId);
        }
        if (activeTab == null) {
            activeTab = channelId;
        }
    }

    public void removeTab(Identifier channelId) {
        tabbedChannels.remove(channelId);
        if (activeTab != null && activeTab.equals(channelId)) {
            activeTab = tabbedChannels.isEmpty() ? null : tabbedChannels.get(0);
        }
    }

    public Identifier getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(Identifier activeTab) {
        if (tabbedChannels.contains(activeTab)) {
            this.activeTab = activeTab;
        }
    }

    public enum DisplayMode {
        FLOATING,
        DOCKED_TOP,
        DOCKED_SIDE
    }
}
