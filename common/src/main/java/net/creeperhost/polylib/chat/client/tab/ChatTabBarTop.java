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

import java.util.ArrayList;
import java.util.List;

public class ChatTabBarTop extends ChatTabBarElement {

    public ChatTabBarTop(GuiParent<?> parent) {
        super(parent);
    }

    private List<ChatTab> collectTabs() {
        List<ChatTab> tabs = new ArrayList<>();
        for (Identifier id : registry.getOrderList(TabPosition.TOP)) {
            if (id.equals(ChatTabRegistry.VANILLA_TAB_ID) && registry.shouldShowVanillaTab()) {
                tabs.add(new VanillaTab());
            } else if (id.equals(ChatTabRegistry.ALL_TAB_ID) && registry.shouldShowAllTab()) {
                tabs.add(new AllTab());
            } else {
                if (registry.getFloatingChannels().contains(id)) continue; // shown as floating window
                ChatTabRegistry.TabEntry e = registry.getEntry(id);
                if (e != null && e.position() == TabPosition.TOP) {
                    tabs.add(new ChannelTab(e.channel()));
                } else {
                    ChatTabRegistry.NotificationTabEntry ne = registry.getNotificationEntry(id);
                    if (ne != null && ne.position() == TabPosition.TOP) tabs.add(new NotificationTab(id));
                }
            }
        }
        return tabs;
    }

    @Override
    protected boolean shouldTearOff(double mouseX, double mouseY) {
        return Math.abs(mouseY - getDragStartY()) > TEAR_OFF_THRESHOLD;
    }

    @Override
    protected void handleCrossBarDrop(@Nullable ChatChannel channel, double mouseX, double mouseY) {
        // Use coordinate checks (isMouseOver is unreliable when drag started on a different element)
        ChatTabBarSide leftBar = ChatTabInjection.getSideBarLeft();
        ChatTabBarSide rightBar = ChatTabInjection.getSideBarRight();
        if (leftBar != null && leftBar.isEnabled() && inBar(leftBar, mouseX, mouseY)) {
            if (channel != null) {
                registry.unregister(channel.getChannelId());
                registry.registerSideLeft(channel);
            } else {
                ChatTab dragging = getDragTab();
                if (dragging instanceof VanillaTab) registry.setVanillaTabPosition(TabPosition.SIDE_LEFT);
                else if (dragging instanceof AllTab) registry.setAllTabPosition(TabPosition.SIDE_LEFT);
            }
        } else if (rightBar != null && rightBar.isEnabled() && inBar(rightBar, mouseX, mouseY)) {
            if (channel != null) {
                registry.unregister(channel.getChannelId());
                registry.registerSideRight(channel);
            } else {
                ChatTab dragging = getDragTab();
                if (dragging instanceof VanillaTab) registry.setVanillaTabPosition(TabPosition.SIDE_RIGHT);
                else if (dragging instanceof AllTab) registry.setAllTabPosition(TabPosition.SIDE_RIGHT);
            }
        }
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (!registry.hasAnyChannel()) return;

        List<ChatTab> tabs = collectTabs();
        tabBoxes.clear();

        Font font = render.font();
        int x = (int) xMin();
        int y = (int) yMin();
        int barHeight = TAB_HEIGHT;

        // Draw bar background
        render.rect(xMin(), yMin(), xSize(), ySize(), BG_COLOR);

        for (ChatTab tab : tabs) {
            String label = getTabLabel(tab);
            int textWidth = font.width(label);
            int tabWidth = textWidth + TAB_PAD * 2 + 4;
            boolean active = isActive(tab);
            boolean hovered = mouseX >= x && mouseX < x + tabWidth && mouseY >= y && mouseY < y + barHeight;

            int color = active ? ACTIVE_COLOR : hovered ? HOVER_COLOR : INACTIVE_COLOR;
            render.rect(x, y, tabWidth, barHeight, color);
            render.drawString(label, x + TAB_PAD + 2, y + 3, 0xFFFFFFFF, true);

            // Pulse border for channel tabs
            if (tab instanceof ChannelTab ct && ChatNotifications.isPulsing(ct.channel().getChannelId())) {
                int pulseColor = PulseEffect.colorForTick(tabTickCounter);
                render.rect(x, y, tabWidth, 2, pulseColor);            // top edge
                render.rect(x, y + barHeight - 2, tabWidth, 2, pulseColor); // bottom edge
                render.rect(x, y, 2, barHeight, pulseColor);            // left edge
                render.rect(x + tabWidth - 2, y, 2, barHeight, pulseColor); // right edge
            }

            // Unread badge for notification tabs
            if (tab instanceof NotificationTab nt) {
                int unread = NotificationWindowRegistry.getUnreadCount(nt.windowId());
                if (unread > 0) {
                    String badge = String.valueOf(Math.min(unread, 99));
                    render.rect(x + tabWidth - 10, y, 10, 8, 0xFFFF4444);
                    render.drawString(badge, x + tabWidth - 9, y + 1, 0xFFFFFFFF, false);
                }
            }

            // Unread badge for notification tabs
            if (tab instanceof NotificationTab nt) {
                int unread = NotificationWindowRegistry.getUnreadCount(nt.windowId());
                if (unread > 0) {
                    String badge = String.valueOf(Math.min(unread, 99));
                    render.rect(x + tabWidth - 10, y, 10, 8, 0xFFFF4444);
                    render.drawString(badge, x + tabWidth - 9, y + 1, 0xFFFFFFFF, false);
                }
            }

            tabBoxes.add(new TabHitBox(x, y, tabWidth, barHeight, tab));
            x += tabWidth + 1;
        }
    }
}
