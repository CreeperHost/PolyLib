package net.creeperhost.polylib.chat.client.notification;

/**
 * Severity level for a notification entry.
 * ALERT entries trigger window border pulse.
 */
public enum NotificationSeverity {
    INFO(0xFF888888),     // gray indicator
    WARNING(0xFFFFAA00),  // yellow indicator
    ALERT(0xFFFF4444);    // red indicator, triggers pulse

    private final int color;

    NotificationSeverity(int color) { this.color = color; }

    public int getColor() { return color; }
}
