package net.creeperhost.polylib.chat;

import net.minecraft.resources.Identifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active chat channels and routes messages.
 */
public class ChatRouter {
    private static final ChatRouter INSTANCE = new ChatRouter();
    
    private final Map<Identifier, ChatChannel> channels = new ConcurrentHashMap<>();

    private ChatRouter() {}

    public static ChatRouter getInstance() {
        return INSTANCE;
    }

    public void registerChannel(ChatChannel channel) {
        channels.put(channel.getChannelId(), channel);
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

    /**
     * Routes a message to a specific channel.
     */
    public boolean routeMessage(Identifier channelId, RichChatMessage message) {
        ChatChannel channel = channels.get(channelId);
        if (channel != null) {
            channel.addMessage(message);
            return true;
        }
        return false;
    }
}
