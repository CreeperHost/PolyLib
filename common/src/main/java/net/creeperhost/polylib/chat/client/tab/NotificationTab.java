package net.creeperhost.polylib.chat.client.tab;

import net.minecraft.resources.Identifier;

/**
 * A tab representing a notification window within the chat tab bar.
 */
public record NotificationTab(Identifier windowId) implements ChatTab {}
