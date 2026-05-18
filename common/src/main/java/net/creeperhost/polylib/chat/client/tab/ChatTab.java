package net.creeperhost.polylib.chat.client.tab;

import net.minecraft.resources.Identifier;

/**
 * Sealed interface for tab types shown in the vanilla chat tab bar.
 */
public sealed interface ChatTab permits VanillaTab, AllTab, ChannelTab, NotificationTab {}
