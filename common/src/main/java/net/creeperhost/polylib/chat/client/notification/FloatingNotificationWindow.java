package net.creeperhost.polylib.chat.client.notification;

import net.creeperhost.polylib.chat.client.ChatWindowManager;
import net.creeperhost.polylib.chat.client.PulseEffect;
import net.creeperhost.polylib.client.modulargui.elements.GuiButton;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.elements.GuiRectangle;
import net.creeperhost.polylib.client.modulargui.elements.GuiScrolling;
import net.creeperhost.polylib.client.modulargui.elements.GuiText;
import net.creeperhost.polylib.client.modulargui.elements.GuiWindow;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Floating notification window — read-only scrollable list of NotificationEntry items.
 * ALERT entries pulse the header yellow→orange. Inherits drag/minimize/resize from GuiWindow.
 */
public class FloatingNotificationWindow extends GuiWindow {

    private static final int NORMAL_HEADER_COLOR = 0xFF333333;

    private final Identifier windowId;
    private final GuiScrolling entryList;
    private final GuiText badgeText;
    private final PulseEffect pulseEffect = new PulseEffect();
    private boolean isPulsing = false;
    private ChatWindowManager windowManager;

    private final NotificationWindowRegistry.NotificationListener listener;

    public FloatingNotificationWindow(GuiParent<?> parent, Identifier windowId) {
        super(parent);
        this.windowId = windowId;

        NotificationWindowRegistry.WindowState state = NotificationWindowRegistry.getWindowState(windowId);
        setTitle(state != null ? state.getTitle() : Component.literal("Notifications"));

        // Unread badge in header, right of title
        this.badgeText = new GuiText(getHeaderBar());
        this.badgeText.constrain(GeoParam.RIGHT, Constraint.relative(getCloseButton().get(GeoParam.LEFT), -4))
                      .constrain(GeoParam.TOP, Constraint.relative(getHeaderBar().get(GeoParam.TOP), 2))
                      .setShadow(true);

        // Clear-all button in header, left of badge
        GuiButton clearAllButton = new GuiButton(getHeaderBar());
        clearAllButton.setOpaque(true);
        clearAllButton.constrain(GeoParam.RIGHT, Constraint.relative(badgeText.get(GeoParam.LEFT), -2))
                      .constrain(GeoParam.TOP, Constraint.relative(getHeaderBar().get(GeoParam.TOP), 2))
                      .constrain(GeoParam.WIDTH, Constraint.literal(14))
                      .constrain(GeoParam.HEIGHT, Constraint.literal(10));
        GuiRectangle clearBg = new GuiRectangle(clearAllButton);
        net.creeperhost.polylib.client.modulargui.lib.Constraints.bind(clearBg, clearAllButton);
        clearBg.fill(() -> clearAllButton.isMouseOver() ? 0xFF666666 : 0xFF444444);
        GuiText clearLabel = new GuiText(clearAllButton, Component.literal("clr"));
        clearLabel.constrain(GeoParam.LEFT, Constraint.relative(clearAllButton.get(GeoParam.LEFT), 1))
                  .constrain(GeoParam.TOP, Constraint.relative(clearAllButton.get(GeoParam.TOP), 1));
        clearAllButton.setLabel(clearLabel);
        clearAllButton.onClick(() -> {
            NotificationWindowRegistry.clearAll(windowId);
            refreshEntries();
        });

        // Scrollable entry list
        this.entryList = new GuiScrolling(getBackground());
        this.entryList.constrain(GeoParam.LEFT, Constraint.match(getBackground().get(GeoParam.LEFT)))
                      .constrain(GeoParam.RIGHT, Constraint.match(getBackground().get(GeoParam.RIGHT)))
                      .constrain(GeoParam.TOP, Constraint.match(getHeaderBar().get(GeoParam.BOTTOM)))
                      .constrain(GeoParam.BOTTOM, Constraint.match(getBackground().get(GeoParam.BOTTOM)));

        // Listen for new entries
        this.listener = (wId, entry) -> {
            if (!wId.equals(windowId)) return;
            refreshEntries();
            if (entry.severity() == NotificationSeverity.ALERT && !entry.read()) {
                isPulsing = true;
                pulseEffect.reset();
                getHeaderBar().fill(() -> pulseEffect.getColor());
            }
        };
        NotificationWindowRegistry.addListener(listener);

        // Close button
        getCloseButton().setOpaque(true);
        getCloseButton().onClick(() -> {
            dispose();
            if (windowManager != null) windowManager.removeWindow(this);
        });

        GuiRectangle closeBg = new GuiRectangle(getCloseButton());
        net.creeperhost.polylib.client.modulargui.lib.Constraints.bind(closeBg, getCloseButton());
        closeBg.fill(() -> getCloseButton().isMouseOver() ? 0xFFCC4444 : 0xFF993333);

        refreshEntries();
    }

    public Identifier getWindowId() { return windowId; }

    public FloatingNotificationWindow setWindowManager(ChatWindowManager manager) {
        this.windowManager = manager;
        return this;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        if (isPulsing) {
            pulseEffect.tick();
            if (!hasUnreadAlerts()) {
                isPulsing = false;
                getHeaderBar().fill(NORMAL_HEADER_COLOR);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        if (isMouseOver() && isPulsing) {
            isPulsing = false;
            getHeaderBar().fill(NORMAL_HEADER_COLOR);
        }
        return result;
    }

    private boolean hasUnreadAlerts() {
        return NotificationWindowRegistry.getEntries(windowId).stream()
                .anyMatch(e -> e.severity() == NotificationSeverity.ALERT && !e.read());
    }

    private void refreshEntries() {
        int unread = NotificationWindowRegistry.getUnreadCount(windowId);
        badgeText.setText(unread > 0
                ? Component.literal("(" + unread + ")")
                : Component.empty());

        GuiElement<?> content = entryList.getContentElement();
        new ArrayList<>(content.getChildren()).forEach(content::removeChild);
        content.constrain(GeoParam.WIDTH, Constraint.match(entryList.get(GeoParam.WIDTH)));

        List<NotificationEntry> entries = NotificationWindowRegistry.getEntries(windowId);
        GuiElement<?> lastElement = null;

        for (NotificationEntry entry : entries) {
            GuiElement<?> row = new GuiElement<>(content);
            row.constrain(GeoParam.LEFT, Constraint.match(content.get(GeoParam.LEFT)));
            row.constrain(GeoParam.RIGHT, Constraint.match(content.get(GeoParam.RIGHT)));
            if (lastElement == null) {
                row.constrain(GeoParam.TOP, Constraint.match(content.get(GeoParam.TOP)));
            } else {
                row.constrain(GeoParam.TOP, Constraint.match(lastElement.get(GeoParam.BOTTOM)));
            }

            // Severity color bar on left edge
            GuiRectangle severityBar = new GuiRectangle(row);
            severityBar.constrain(GeoParam.LEFT, Constraint.match(row.get(GeoParam.LEFT)))
                       .constrain(GeoParam.TOP, Constraint.match(row.get(GeoParam.TOP)))
                       .constrain(GeoParam.BOTTOM, Constraint.match(row.get(GeoParam.BOTTOM)))
                       .constrain(GeoParam.WIDTH, Constraint.literal(3))
                       .fill(entry.read() ? 0xFF444444 : entry.severity().getColor());

            // Title
            GuiText titleText = new GuiText(row);
            titleText.setText(entry.title());
            titleText.constrain(GeoParam.LEFT, Constraint.relative(row.get(GeoParam.LEFT), 6))
                     .constrain(GeoParam.TOP, Constraint.relative(row.get(GeoParam.TOP), 2))
                     .constrain(GeoParam.RIGHT, Constraint.relative(row.get(GeoParam.RIGHT), -44))
                     .setShadow(true);

            // Timestamp (right-aligned)
            String timeStr = new SimpleDateFormat("HH:mm").format(new Date(entry.timestamp()));
            GuiText timeText = new GuiText(row, Component.literal(timeStr));
            timeText.constrain(GeoParam.RIGHT, Constraint.relative(row.get(GeoParam.RIGHT), -4))
                    .constrain(GeoParam.TOP, Constraint.relative(row.get(GeoParam.TOP), 2))
                    .setAlignment(Align.MAX);

            // Body
            GuiText bodyText = new GuiText(row);
            bodyText.setText(entry.body());
            bodyText.constrain(GeoParam.LEFT, Constraint.relative(row.get(GeoParam.LEFT), 6))
                    .constrain(GeoParam.TOP, Constraint.relative(titleText.get(GeoParam.BOTTOM), 1))
                    .constrain(GeoParam.RIGHT, Constraint.relative(row.get(GeoParam.RIGHT), -4))
                    .setWrap(true).setAlignment(Align.MIN).autoHeight();

            row.constrain(GeoParam.HEIGHT, Constraint.relative(bodyText.get(GeoParam.HEIGHT), 14));

            // Row background (dim if read)
            GuiRectangle rowBg = new GuiRectangle(row);
            net.creeperhost.polylib.client.modulargui.lib.Constraints.bind(rowBg, row);
            rowBg.fill(entry.read() ? 0x22FFFFFF : 0x44FFFFFF);

            // Clickable overlay to mark read
            final NotificationEntry entryRef = entry;
            if (!entry.read()) {
                GuiButton rowBtn = new GuiButton(row);
                rowBtn.constrain(GeoParam.LEFT, Constraint.match(row.get(GeoParam.LEFT)))
                      .constrain(GeoParam.RIGHT, Constraint.match(row.get(GeoParam.RIGHT)))
                      .constrain(GeoParam.TOP, Constraint.match(row.get(GeoParam.TOP)))
                      .constrain(GeoParam.BOTTOM, Constraint.match(row.get(GeoParam.BOTTOM)));
                rowBtn.onClick(() -> {
                    NotificationWindowRegistry.markRead(windowId, entryRef.entryId());
                    refreshEntries();
                });
            }

            lastElement = row;
        }
    }

    /**
     * Unregister listeners. Call before closing/removing this window.
     */
    public void dispose() {
        NotificationWindowRegistry.removeListener(listener);
    }
}
