package net.creeperhost.polylib.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a member/participant in a ChatChannel sidebar.
 * Can represent a Player, a Shell, or an Automation Controller.
 */
public record ChatMember(
        UUID memberId,
        Component displayName,
        @Nullable Identifier icon,
        boolean isOnline
) {
    public ChatMember withOnlineStatus(boolean online) {
        return new ChatMember(memberId, displayName, icon, online);
    }
}
