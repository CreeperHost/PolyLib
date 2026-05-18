package net.creeperhost.polylib.chat.client.tab;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.client.ChatNotifications;
import net.creeperhost.polylib.chat.client.PulseEffect;
import net.creeperhost.polylib.chat.client.notification.NotificationWindowRegistry;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

public class ChatTabBarSide extends ChatTabBarElement {
    private static final int TAB_WIDTH = 16;
    private final TabPosition side;

    public ChatTabBarSide(GuiParent<?> parent, TabPosition side) {
        super(parent);
        this.side = side;
    }

    public TabPosition getSide() { return side; }

    private List<ChatTab> collectTabs() {
        List<ChatTab> tabs = new ArrayList<>();
        for (Identifier id : registry.getOrderList(side)) {
            if (id.equals(ChatTabRegistry.VANILLA_TAB_ID) && registry.shouldShowVanillaTab()) {
                tabs.add(new VanillaTab());
            } else if (id.equals(ChatTabRegistry.ALL_TAB_ID) && registry.shouldShowAllTab()) {
                tabs.add(new AllTab());
            } else {
                if (registry.getFloatingChannels().contains(id)) continue; // shown as floating window
                ChatTabRegistry.TabEntry e = registry.getEntry(id);
                if (e != null && e.position() == side) {
                    tabs.add(new ChannelTab(e.channel()));
                } else {
                    ChatTabRegistry.NotificationTabEntry ne = registry.getNotificationEntry(id);
                    if (ne != null && ne.position() == side) tabs.add(new NotificationTab(id));
                }
            }
        }
        return tabs;
    }

    @Override
    protected boolean shouldTearOff(double mouseX, double mouseY) {
        return Math.abs(mouseX - getDragStartX()) > TEAR_OFF_THRESHOLD;
    }

    @Override
    protected void handleCrossBarDrop(@Nullable ChatChannel channel, double mouseX, double mouseY) {
        ChatTabBarTop topBar = ChatTabInjection.getTopBar();
        ChatTabBarSide leftBar = ChatTabInjection.getSideBarLeft();
        ChatTabBarSide rightBar = ChatTabInjection.getSideBarRight();
        if (topBar != null && inBar(topBar, mouseX, mouseY)) {
            if (channel != null) {
                registry.unregister(channel.getChannelId());
                registry.registerTop(channel);
            } else {
                ChatTab dragging = getDragTab();
                if (dragging instanceof VanillaTab) registry.setVanillaTabPosition(TabPosition.TOP);
                else if (dragging instanceof AllTab) registry.setAllTabPosition(TabPosition.TOP);
            }
        } else if (side == TabPosition.SIDE_LEFT && rightBar != null && rightBar.isEnabled() && inBar(rightBar, mouseX, mouseY)) {
            if (channel != null) { registry.unregister(channel.getChannelId()); registry.registerSideRight(channel); }
            else { ChatTab d = getDragTab(); if (d instanceof VanillaTab) registry.setVanillaTabPosition(TabPosition.SIDE_RIGHT); else if (d instanceof AllTab) registry.setAllTabPosition(TabPosition.SIDE_RIGHT); }
        } else if (side == TabPosition.SIDE_RIGHT && leftBar != null && leftBar.isEnabled() && inBar(leftBar, mouseX, mouseY)) {
            if (channel != null) { registry.unregister(channel.getChannelId()); registry.registerSideLeft(channel); }
            else { ChatTab d = getDragTab(); if (d instanceof VanillaTab) registry.setVanillaTabPosition(TabPosition.SIDE_LEFT); else if (d instanceof AllTab) registry.setAllTabPosition(TabPosition.SIDE_LEFT); }
        }
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        List<ChatTab> tabs = collectTabs();
        tabBoxes.clear();
        if (tabs.isEmpty()) return;

        Font font = render.font();
        int x = (int) xMin();
        int y = (int) yMin();

        // Draw bar background
        render.rect(xMin(), yMin(), (double) TAB_WIDTH, ySize(), BG_COLOR);

        for (ChatTab tab : tabs) {
            String label = getTabLabel(tab);
            int textWidth = font.width(label);
            int tabHeight = textWidth + TAB_PAD * 2 + 4;
            boolean active = isActive(tab);
            boolean hovered = mouseX >= x && mouseX < x + TAB_WIDTH && mouseY >= y && mouseY < y + tabHeight;

            int color = active ? ACTIVE_COLOR : hovered ? HOVER_COLOR : INACTIVE_COLOR;
            render.rect(x, y, TAB_WIDTH, tabHeight, color);

            // Pulse border for channel tabs
            if (tab instanceof ChannelTab ct && ChatNotifications.isPulsing(ct.channel().getChannelId())) {
                int pulseColor = PulseEffect.colorForTick(tabTickCounter);
                render.rect(x, y, TAB_WIDTH, 2, pulseColor);              // top edge
                render.rect(x, y + tabHeight - 2, TAB_WIDTH, 2, pulseColor); // bottom edge
                render.rect(x, y, 2, tabHeight, pulseColor);              // left edge
                render.rect(x + TAB_WIDTH - 2, y, 2, tabHeight, pulseColor); // right edge
            }

            // Unread badge for notification tabs
            if (tab instanceof NotificationTab nt) {
                int unread = NotificationWindowRegistry.getUnreadCount(nt.windowId());
                if (unread > 0) {
                    render.rect(x, y, TAB_WIDTH, 8, 0xFFFF4444);
                    render.drawString(String.valueOf(Math.min(unread, 99)), x + 1, y + 1, 0xFFFFFFFF, false);
                }
            }

            // Unread badge for notification tabs
            if (tab instanceof NotificationTab nt) {
                int unread = NotificationWindowRegistry.getUnreadCount(nt.windowId());
                if (unread > 0) {
                    render.rect(x, y, TAB_WIDTH, 8, 0xFFFF4444);
                    render.drawString(String.valueOf(Math.min(unread, 99)), x + 1, y + 1, 0xFFFFFFFF, false);
                }
            }

            // Rotate text 90° for vertical display
            Matrix3x2fStack poses = render.pose();
            poses.pushMatrix();
            poses.translate((float) (x + TAB_WIDTH - 3), (float) (y + TAB_PAD + 2));
            poses.rotate((float) Math.toRadians(90));
            render.drawString(label, 0, 0, 0xFFFFFFFF, true);
            poses.popMatrix();

            tabBoxes.add(new TabHitBox(x, y, TAB_WIDTH, tabHeight, tab));
            y += tabHeight + 1;
        }
    }
}
