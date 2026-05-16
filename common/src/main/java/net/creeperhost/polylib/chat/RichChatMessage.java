package net.creeperhost.polylib.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single message in the PolyLib Modular Chat Framework.
 */
public record RichChatMessage(
        UUID messageId,
        Component content,
        Instant timestamp,
        Component senderName,
        @Nullable Identifier senderIcon,
        @Nullable UUID senderId
) {
    public static RichChatMessage create(Component content, Component senderName) {
        return new RichChatMessage(UUID.randomUUID(), content, Instant.now(), senderName, null, null);
    }

    public static RichChatMessage create(Component content, Component senderName, Identifier senderIcon) {
        return new RichChatMessage(UUID.randomUUID(), content, Instant.now(), senderName, senderIcon, null);
    }

    public static RichChatMessage create(Component content, Component senderName, Identifier senderIcon, UUID senderId) {
        return new RichChatMessage(UUID.randomUUID(), content, Instant.now(), senderName, senderIcon, senderId);
    }
}
