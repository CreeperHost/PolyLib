package net.creeperhost.polylib.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents an isolated stream of messages and members.
 */
public class ChatChannel {
    private final Identifier channelId;
    private final Component name;
    private final boolean canReply;

    private final List<ChatMember> members = new ArrayList<>();
    private final List<RichChatMessage> messages = new ArrayList<>();
    
    private final List<Consumer<RichChatMessage>> messageListeners = new ArrayList<>();

    public ChatChannel(Identifier channelId, Component name, boolean canReply) {
        this.channelId = channelId;
        this.name = name;
        this.canReply = canReply;
    }

    public Identifier getChannelId() {
        return channelId;
    }

    public Component getName() {
        return name;
    }

    public boolean canReply() {
        return canReply;
    }

    public List<ChatMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public void addMember(ChatMember member) {
        members.removeIf(m -> m.memberId().equals(member.memberId()));
        members.add(member);
    }

    public void removeMember(UUID memberId) {
        members.removeIf(m -> m.memberId().equals(memberId));
    }

    public Optional<ChatMember> getMember(UUID memberId) {
        return members.stream().filter(m -> m.memberId().equals(memberId)).findFirst();
    }

    public List<RichChatMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(RichChatMessage message) {
        this.messages.add(message);
        // keep buffer size reasonable
        if (this.messages.size() > 1000) {
            this.messages.remove(0);
        }
        for (Consumer<RichChatMessage> listener : messageListeners) {
            listener.accept(message);
        }
    }

    public void addMessageListener(Consumer<RichChatMessage> listener) {
        this.messageListeners.add(listener);
    }

    public void removeMessageListener(Consumer<RichChatMessage> listener) {
        this.messageListeners.remove(listener);
    }

    private boolean mentionPulseEnabled = true;
    private boolean vanillaMentionNotification = true;

    public boolean isMentionPulseEnabled() { return mentionPulseEnabled; }
    public void setMentionPulseEnabled(boolean enabled) { this.mentionPulseEnabled = enabled; }

    public boolean isVanillaMentionNotification() { return vanillaMentionNotification; }
    public void setVanillaMentionNotification(boolean enabled) { this.vanillaMentionNotification = enabled; }

    private Consumer<String> messageSubmitHandler;

    public void setSubmitHandler(Consumer<String> handler) {
        this.messageSubmitHandler = handler;
    }

    public void submitMessage(String message) {
        if (messageSubmitHandler != null) {
            messageSubmitHandler.accept(message);
        }
    }
}
