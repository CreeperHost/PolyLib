package net.creeperhost.polylib.chat.client.tab;

import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.chat.client.ChatNotifications;
import net.creeperhost.polylib.chat.client.ChatWindowManager;
import net.creeperhost.polylib.chat.client.FloatingChatWindow;
import net.creeperhost.polylib.chat.client.notification.NotificationEntry;
import net.creeperhost.polylib.chat.client.notification.NotificationWindowRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class ChatTabInjection {

    private static ChatTabBarTop topBar;
    private static ChatTabBarSide sideBarLeft;
    private static ChatTabBarSide sideBarRight;
    private static ChatWindowManager windowManager;

    private ChatTabInjection() {}

    public static ChatTabBarTop getTopBar() { return topBar; }
    public static ChatTabBarSide getSideBarLeft() { return sideBarLeft; }
    public static ChatTabBarSide getSideBarRight() { return sideBarRight; }

    public static void init() {
        ChatNotifications.init(); // wire mention-pulse global listener
        ModularGuiInjector.registerInjection(
            screen -> screen instanceof ChatScreen,
            screen -> new ChatTabProvider()
        );
    }

    private static class ChatTabProvider implements GuiProvider {

        @Override
        public void buildGui(ModularGui gui) {
            gui.initFullscreenGui();
            GuiElement<?> root = gui.getRoot();
            ChatTabRegistry reg = ChatTabRegistry.get();

            if (!reg.hasAnyChannel()) {
                topBar = null;
                sideBarLeft = null;
                sideBarRight = null;
                return;
            }

            // TOP tab bar: horizontal strip above the chat input box
            topBar = new ChatTabBarTop(root);
            topBar.constrain(GeoParam.LEFT, Constraint.literal(2));
            topBar.constrain(GeoParam.RIGHT, Constraint.relative(root.get(GeoParam.RIGHT), -2));
            topBar.constrain(GeoParam.BOTTOM, Constraint.relative(root.get(GeoParam.BOTTOM), -14));
            topBar.constrain(GeoParam.HEIGHT, Constraint.literal(14));

            // SIDE LEFT tab bar — always created so docking always works; hidden when empty and no window is being dragged
            sideBarLeft = new ChatTabBarSide(root, TabPosition.SIDE_LEFT);
            sideBarLeft.constrain(GeoParam.LEFT, Constraint.literal(0));
            sideBarLeft.constrain(GeoParam.WIDTH, Constraint.literal(16));
            sideBarLeft.constrain(GeoParam.BOTTOM, Constraint.relative(topBar.get(GeoParam.TOP), 0));
            sideBarLeft.constrain(GeoParam.HEIGHT, Constraint.literal(200));
            sideBarLeft.setEnabled(() -> !reg.getSideLeftEntries().isEmpty() || FloatingChatWindow.anyWindowDragging);

            // SIDE RIGHT tab bar — always created so docking always works; hidden when empty and no window is being dragged
            sideBarRight = new ChatTabBarSide(root, TabPosition.SIDE_RIGHT);
            sideBarRight.constrain(GeoParam.RIGHT, Constraint.relative(root.get(GeoParam.RIGHT), -2));
            sideBarRight.constrain(GeoParam.WIDTH, Constraint.literal(16));
            sideBarRight.constrain(GeoParam.BOTTOM, Constraint.relative(topBar.get(GeoParam.TOP), 0));
            sideBarRight.constrain(GeoParam.HEIGHT, Constraint.literal(200));
            sideBarRight.setEnabled(() -> !reg.getSideRightEntries().isEmpty() || FloatingChatWindow.anyWindowDragging);

            // Channel message history overlay — insets so it never covers active side bars
            // left offset: 2 normally, 18 when left bar has tabs; right offset: -2 normally, -18 when right bar has tabs
            ChannelHistoryOverlay history = new ChannelHistoryOverlay(root);
            history.constrain(GeoParam.LEFT, Constraint.dynamic(() -> (double)(!reg.getSideLeftEntries().isEmpty() ? 18 : 2)));
            history.constrain(GeoParam.RIGHT, Constraint.relative(root.get(GeoParam.RIGHT), 0))
                   .constrain(GeoParam.RIGHT, Constraint.dynamic(() -> root.get(GeoParam.RIGHT).get() + (!reg.getSideRightEntries().isEmpty() ? -18 : -2)));
            history.constrain(GeoParam.BOTTOM, Constraint.relative(root.get(GeoParam.BOTTOM), -28));
            history.constrain(GeoParam.HEIGHT, Constraint.literal(200));

            // Wire tear-off: dragging a tab out of the bar creates a floating window.
            // After adding the floating window, re-adopt side bars to push them to the top of the render stack.
            windowManager = new ChatWindowManager();
            ChatTabBarSide finalSideBarLeft = sideBarLeft;
            ChatTabBarSide finalSideBarRight = sideBarRight;

            // Restore any windows that were floating when chat was last closed
            int restoreOffset = 0;
            for (Identifier floatingId : reg.getFloatingChannels()) {
                ChatTabRegistry.TabEntry entry = reg.getEntry(floatingId);
                if (entry == null) continue;
                FloatingChatWindow restored = new FloatingChatWindow(root, entry.channel());
                double[] pos = FloatingChatWindow.SAVED_POSITIONS.get(floatingId.toString());
                if (pos != null) {
                    restored.constrain(GeoParam.LEFT, Constraint.literal(pos[0]))
                            .constrain(GeoParam.TOP, Constraint.literal(pos[1]))
                            .constrain(GeoParam.WIDTH, Constraint.literal(pos[2]))
                            .constrain(GeoParam.HEIGHT, Constraint.literal(pos[3]));
                } else {
                    restored.constrain(GeoParam.LEFT, Constraint.literal(20 + restoreOffset * 20))
                            .constrain(GeoParam.TOP, Constraint.literal(20 + restoreOffset * 20))
                            .constrain(GeoParam.WIDTH, Constraint.literal(280))
                            .constrain(GeoParam.HEIGHT, Constraint.literal(200));
                }
                restored.applyRestoredState(); // restore minimized state now that bounds are resolved
                windowManager.addWindow(restored);
                restoreOffset++;
            }

            reg.setTearOffCallback(event -> {
                reg.markFloating(event.channel().getChannelId());
                FloatingChatWindow win = new FloatingChatWindow(root, event.channel());
                win.setSourceTabPosition(event.sourcePosition());
                win.constrain(GeoParam.LEFT, Constraint.literal((int) event.x() - 140))
                   .constrain(GeoParam.TOP, Constraint.literal((int) event.y() - 10))
                   .constrain(GeoParam.WIDTH, Constraint.literal(280))
                   .constrain(GeoParam.HEIGHT, Constraint.literal(200));
                windowManager.addWindow(win);
                win.startDragging();
                // Re-adopt side bars so they render after (on top of) the newly added floating window
                root.adoptChild(finalSideBarLeft);
                root.adoptChild(finalSideBarRight);
            });
        }
    }

    static class ChannelHistoryOverlay extends GuiElement<ChannelHistoryOverlay> implements BackgroundRender {
        public ChannelHistoryOverlay(GuiParent<?> parent) {
            super(parent);
        }

        @Override
        public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            ChatTabRegistry reg = ChatTabRegistry.get();
            ChatTab active = reg.getActiveTab();

            if (active instanceof VanillaTab) return;

            // Background
            render.rect(xMin(), yMin(), xSize(), ySize(), 0xCC000000);

            if (active instanceof NotificationTab nt) {
                renderNotifications(render, nt);
                return;
            }

            List<RichChatMessage> messages = getMessagesForTab(active);

            Font font = render.font();
            int lineHeight = 10;
            int y = (int) yMax() - 4;

            for (int i = messages.size() - 1; i >= 0 && y > yMin(); i--) {
                RichChatMessage msg = messages.get(i);
                Component display = formatMessage(msg);
                y -= lineHeight;
                render.drawString(display, (double) ((int) xMin() + 4), (double) y, 0xFFFFFFFF, true);
            }
        }

        private List<RichChatMessage> getMessagesForTab(ChatTab tab) {
            return switch (tab) {
                case VanillaTab v -> List.of();
                case AllTab a -> {
                    List<RichChatMessage> all = new ArrayList<>();
                    for (var entry : ChatTabRegistry.get().allEntries()) {
                        all.addAll(entry.channel().getMessages());
                    }
                    all.sort(Comparator.comparing(RichChatMessage::timestamp));
                    yield all;
                }
                case ChannelTab ct -> ct.channel().getMessages();
                case NotificationTab nt -> List.of();
            };
        }

        private Component formatMessage(RichChatMessage msg) {
            if (msg.senderName() != null) {
                return Component.literal("<")
                    .append(msg.senderName())
                    .append("> ")
                    .append(msg.content());
            }
            return msg.content();
        }

        private void renderNotifications(GuiRender render, NotificationTab nt) {
            List<NotificationEntry> entries = NotificationWindowRegistry.getEntries(nt.windowId());
            Font font = render.font();
            int lineHeight = 10;
            int y = (int) yMax() - 4;
            for (int i = entries.size() - 1; i >= 0 && y > yMin(); i--) {
                NotificationEntry entry = entries.get(i);
                // Severity indicator color
                int color = entry.read() ? 0xFF888888 : entry.severity().getColor();
                Component line = Component.literal("\u2022 ")
                    .withStyle(s -> s.withColor(color))
                    .append(entry.title())
                    .append(Component.literal(": "))
                    .append(entry.body());
                y -= lineHeight;
                render.drawString(line, (double)((int) xMin() + 4), (double) y, 0xFFFFFFFF, true);
            }
        }
    }
}
