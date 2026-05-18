package net.creeperhost.polylib.chat.client;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatConfig;
import net.creeperhost.polylib.chat.ChatRouter;
import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.chat.client.tab.ChatTabRegistry;
import net.creeperhost.polylib.chat.client.tab.ChatTab;
import net.creeperhost.polylib.chat.client.tab.ChannelTab;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.function.Predicate;

/**
 * Registry for mention/notification triggers per channel.
 * When a message arrives on a channel, the registered trigger is tested.
 * If it fires, the channel's UI elements pulse and an optional vanilla notification is sent.
 */
public class ChatNotifications {

    /** Fires on every new message. Opt-in only. */
    public static final Predicate<RichChatMessage> TRIGGER_ANY_MESSAGE = msg -> true;

    /** Fires when message content contains the local player's name. Default trigger. */
    public static final Predicate<RichChatMessage> TRIGGER_CONTAINS_NAME = msg -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        String playerName = mc.player.getScoreboardName();
        return msg.content().getString().toLowerCase(Locale.ROOT)
                  .contains(playerName.toLowerCase(Locale.ROOT));
    };

    private static final Map<Identifier, Predicate<RichChatMessage>> triggers = new HashMap<>();
    private static final Set<Identifier> pulsing = new HashSet<>();
    private static final List<PulseListener> pulseListeners = new ArrayList<>();

    /**
     * Register a custom trigger for a channel's notification pulse.
     * Replaces any previously registered trigger for that channel.
     * If no trigger is registered, {@link #TRIGGER_CONTAINS_NAME} is used as default.
     */
    public static void registerMentionTrigger(Identifier channelId, Predicate<RichChatMessage> trigger) {
        triggers.put(channelId, trigger);
    }

    /**
     * Remove a custom trigger. Channel reverts to default {@link #TRIGGER_CONTAINS_NAME}.
     */
    public static void removeMentionTrigger(Identifier channelId) {
        triggers.remove(channelId);
    }

    /**
     * Test whether a message should trigger a pulse for the given channel.
     */
    public static boolean shouldPulse(Identifier channelId, RichChatMessage message) {
        Predicate<RichChatMessage> trigger = triggers.getOrDefault(channelId, TRIGGER_CONTAINS_NAME);
        return trigger.test(message);
    }

    /**
     * Mark a channel as pulsing. UI elements observe this via {@link PulseListener}.
     */
    public static void startPulse(Identifier channelId) {
        if (pulsing.add(channelId)) {
            List<PulseListener> snapshot = new ArrayList<>(pulseListeners);
            for (PulseListener l : snapshot) {
                l.onPulseChanged(channelId, true);
            }
        }
    }

    /**
     * Clear the pulse state (user clicked/focused the channel).
     */
    public static void clearPulse(Identifier channelId) {
        if (pulsing.remove(channelId)) {
            List<PulseListener> snapshot = new ArrayList<>(pulseListeners);
            for (PulseListener l : snapshot) {
                l.onPulseChanged(channelId, false);
            }
        }
    }

    /**
     * Check if a channel is currently pulsing.
     */
    public static boolean isPulsing(Identifier channelId) {
        return pulsing.contains(channelId);
    }

    public static void addPulseListener(PulseListener listener) {
        pulseListeners.add(listener);
    }

    public static void removePulseListener(PulseListener listener) {
        pulseListeners.remove(listener);
    }

    /**
     * Called when any channel receives a new message. Evaluates the trigger
     * and starts pulse / sends vanilla notification as appropriate.
     */
    public static void onMessageReceived(Identifier channelId, RichChatMessage message,
                                         boolean channelIsFocused, boolean mentionPulseEnabled,
                                         boolean vanillaNotifyEnabled, Component channelName) {
        if (!ChatConfig.mentionPulse) return;
        if (!shouldPulse(channelId, message)) return;

        if (mentionPulseEnabled && !channelIsFocused) {
            startPulse(channelId);
        }

        if (vanillaNotifyEnabled && ChatConfig.vanillaMentionNotify && !channelIsFocused) {
            sendVanillaNotification(channelName);
        }
    }

    private static void sendVanillaNotification(Component channelName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(
                Component.literal("\u00a7e[")
                    .append(channelName)
                    .append(Component.literal("] You have been mentioned"))
            );
        }
    }

    /**
     * Call once during client init to hook into ChatRouter for global pulse monitoring.
     */
    public static void init() {
        ChatRouter.getInstance().addGlobalMessageListener((channel, message) -> {
            boolean focused = isChannelFocused(channel.getChannelId());
            onMessageReceived(
                channel.getChannelId(), message, focused,
                channel.isMentionPulseEnabled(),
                channel.isVanillaMentionNotification(),
                channel.getName()
            );
        });
    }

    private static boolean isChannelFocused(Identifier channelId) {
        ChatTabRegistry registry = ChatTabRegistry.get();
        ChatTab activeTab = registry.getActiveTab();
        if (activeTab instanceof ChannelTab ct
                && ct.channel().getChannelId().equals(channelId)) {
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public interface PulseListener {
        void onPulseChanged(Identifier channelId, boolean pulsing);
    }
}
