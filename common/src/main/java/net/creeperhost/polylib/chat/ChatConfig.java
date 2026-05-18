package net.creeperhost.polylib.chat;

/**
 * User-facing config options for the PolyLib chat system.
 * Values are runtime defaults; these can be wired to a config screen in the future.
 */
public class ChatConfig {

    /** Enable mention pulse animation on floating windows and tab badges. */
    public static boolean mentionPulse = true;

    /** Enable vanilla system-message notification for mentions in non-active tabs. */
    public static boolean vanillaMentionNotify = true;

    private ChatConfig() {}
}
