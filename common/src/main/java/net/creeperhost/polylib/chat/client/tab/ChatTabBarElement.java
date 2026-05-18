package net.creeperhost.polylib.chat.client.tab;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.client.ChatNotifications;
import net.creeperhost.polylib.chat.client.PulseEffect;
import net.creeperhost.polylib.chat.client.notification.NotificationWindowRegistry;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatTabBarElement extends GuiElement<ChatTabBarElement> implements BackgroundRender {
    protected static final int TAB_HEIGHT = 14;
    protected static final int TAB_PAD = 2;
    protected static final int BG_COLOR = 0xCC202020;
    protected static final int ACTIVE_COLOR = 0xFF4488CC;
    protected static final int HOVER_COLOR = 0xFF336699;
    protected static final int INACTIVE_COLOR = 0xFF333333;
    protected static final int TEAR_OFF_THRESHOLD = 20;

    protected final ChatTabRegistry registry;
    protected final List<TabHitBox> tabBoxes = new ArrayList<>();

    // Drag tracking for tear-off / rearrange
    private boolean dragTracking = false;
    private double dragStartX, dragStartY;
    private ChatTab dragTab;
    private ChatChannel dragChannel; // null for VanillaTab / AllTab

    // Tick counter for pulse animation
    protected int tabTickCounter = 0;

    public ChatTabBarElement(GuiParent<?> parent) {
        super(parent);
        this.registry = ChatTabRegistry.get();
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        tabTickCounter++;
    }

    protected boolean isActive(ChatTab tab) {
        ChatTab active = registry.getActiveTab();
        return switch (tab) {
            case VanillaTab v -> active instanceof VanillaTab;
            case AllTab a -> active instanceof AllTab;
            case ChannelTab ct -> active instanceof ChannelTab act
                && ct.channel().getChannelId().equals(act.channel().getChannelId());
            case NotificationTab nt -> active instanceof NotificationTab ant
                && nt.windowId().equals(ant.windowId());
        };
    }

    protected String getTabLabel(ChatTab tab) {
        return switch (tab) {
            case VanillaTab v -> "Vanilla";
            case AllTab a -> "All";
            case ChannelTab ct -> ct.channel().getName().getString();
            case NotificationTab nt -> {
                ChatTabRegistry.NotificationTabEntry ne = registry.getNotificationEntry(nt.windowId());
                yield ne != null ? ne.title().getString() : "Notifications";
            }
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        for (TabHitBox box : tabBoxes) {
            if (mouseX >= box.x && mouseX < box.x + box.w && mouseY >= box.y && mouseY < box.y + box.h) {
                registry.setActiveTab(box.tab);
                // Clear pulse when a channel tab is clicked
                if (box.tab instanceof ChannelTab ct) {
                    ChatNotifications.clearPulse(ct.channel().getChannelId());
                }
                // Start drag tracking for all tab types
                dragTracking = true;
                dragStartX = mouseX;
                dragStartY = mouseY;
                dragTab = box.tab;
                dragChannel = (box.tab instanceof ChannelTab ct) ? ct.channel() : null;
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if (!dragTracking || dragTab == null) return;

        if (dragTab instanceof ChannelTab && shouldTearOff(mouseX, mouseY) && registry.hasTearOffCallback()) {
            // Capture source position; do NOT unregister — the channel must stay in entries
            // so it can be found by the restore loop when chat is reopened.
            // markFloating() is called inside the tear-off callback.
            TabPosition sourcePos = registry.getTabPosition(dragChannel.getChannelId());
            if (sourcePos == null) sourcePos = TabPosition.TOP;
            registry.fireTearOffCallback(dragChannel, mouseX, mouseY, sourcePos);
            dragTracking = false;
            dragTab = null;
            dragChannel = null;
            return;
        }

        // Drag-to-reorder: swap any tab (Vanilla, All, or channel) when cursor enters another tab's bounds
        Identifier dragId = dragTab instanceof ChannelTab ct ? ct.channel().getChannelId()
                          : dragTab instanceof VanillaTab   ? ChatTabRegistry.VANILLA_TAB_ID
                          : dragTab instanceof AllTab       ? ChatTabRegistry.ALL_TAB_ID
                          : null;
        if (dragId != null) {
            for (TabHitBox box : tabBoxes) {
                if (mouseX >= box.x && mouseX < box.x + box.w && mouseY >= box.y && mouseY < box.y + box.h) {
                    Identifier boxId = box.tab instanceof ChannelTab ct2 ? ct2.channel().getChannelId()
                                     : box.tab instanceof VanillaTab     ? ChatTabRegistry.VANILLA_TAB_ID
                                     : box.tab instanceof AllTab         ? ChatTabRegistry.ALL_TAB_ID
                                     : null;
                    if (boxId != null && !boxId.equals(dragId)) {
                        registry.swapOrder(dragId, boxId);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragTracking && dragTab != null) {
            // Check for cross-bar drag
            handleCrossBarDrop(dragChannel, mouseX, mouseY);
            dragTracking = false;
            dragTab = null;
            dragChannel = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Subclasses implement this to determine tear-off direction.
     * Top bar tears off on vertical drag, side bar on horizontal drag.
     */
    protected abstract boolean shouldTearOff(double mouseX, double mouseY);

    /**
     * Subclasses implement this to check if drop is over the opposite bar.
     * {@code channel} is null for VanillaTab / AllTab drags.
     */
    protected abstract void handleCrossBarDrop(@Nullable ChatChannel channel, double mouseX, double mouseY);

    protected double getDragStartX() { return dragStartX; }
    protected double getDragStartY() { return dragStartY; }
    protected ChatTab getDragTab() { return dragTab; }

    /** True if (mouseX, mouseY) falls within the bounds of the given bar element. */
    protected static boolean inBar(ChatTabBarElement bar, double mouseX, double mouseY) {
        return mouseX >= bar.xMin() && mouseX <= bar.xMax()
            && mouseY >= bar.yMin() && mouseY <= bar.yMax();
    }

    protected record TabHitBox(int x, int y, int w, int h, ChatTab tab) {}
}
