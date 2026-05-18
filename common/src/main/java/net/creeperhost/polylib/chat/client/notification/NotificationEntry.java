package net.creeperhost.polylib.chat.client.notification;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A single notification pushed by a mod.
 */
public record NotificationEntry(
    UUID entryId,
    Identifier sourceId,
    Component title,
    Component body,
    @Nullable Identifier icon,
    long timestamp,
    NotificationSeverity severity,
    boolean read
) {
    public NotificationEntry markRead() {
        return new NotificationEntry(entryId, sourceId, title, body, icon, timestamp, severity, true);
    }

    public static NotificationEntry create(Identifier sourceId, Component title, Component body,
                                           NotificationSeverity severity) {
        return new NotificationEntry(UUID.randomUUID(), sourceId, title, body, null,
                System.currentTimeMillis(), severity, false);
    }

    public static NotificationEntry create(Identifier sourceId, Component title, Component body,
                                           @Nullable Identifier icon, NotificationSeverity severity) {
        return new NotificationEntry(UUID.randomUUID(), sourceId, title, body, icon,
                System.currentTimeMillis(), severity, false);
    }
}
