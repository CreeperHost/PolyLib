package net.creeperhost.polylib.chat;

import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Manages active chat channels and routes messages.
 */
public class ChatRouter {
    private static final ChatRouter INSTANCE = new ChatRouter();
    
    private final Map<Identifier, ChatChannel> channels = new ConcurrentHashMap<>();
    private final List<BiConsumer<ChatChannel, RichChatMessage>> globalMessageListeners = new ArrayList<>();

    private ChatRouter() {}

    public static ChatRouter getInstance() {
        return INSTANCE;
    }

    public void registerChannel(ChatChannel channel) {
        channels.put(channel.getChannelId(), channel);
        // Bridge: fire global listeners whenever a message is added to this channel directly
        channel.addMessageListener(msg -> {
            for (BiConsumer<ChatChannel, RichChatMessage> listener : new ArrayList<>(globalMessageListeners)) {
                listener.accept(channel, msg);
            }
        });
    }

    public void unregisterChannel(Identifier channelId) {
        channels.remove(channelId);
    }

    public ChatChannel getChannel(Identifier channelId) {
        return channels.get(channelId);
    }

    public Collection<ChatChannel> getActiveChannels() {
        return Collections.unmodifiableCollection(channels.values());
    }

    public void addGlobalMessageListener(BiConsumer<ChatChannel, RichChatMessage> listener) {
        globalMessageListeners.add(listener);
    }

    public void removeGlobalMessageListener(BiConsumer<ChatChannel, RichChatMessage> listener) {
        globalMessageListeners.remove(listener);
    }

    /**
     * Routes a message to a specific channel.
     */
    public boolean routeMessage(Identifier channelId, RichChatMessage message) {
        ChatChannel channel = channels.get(channelId);
        if (channel != null) {
            channel.addMessage(message);
            for (BiConsumer<ChatChannel, RichChatMessage> listener : new ArrayList<>(globalMessageListeners)) {
                listener.accept(channel, message);
            }
            return true;
        }
        return false;
    }
}
