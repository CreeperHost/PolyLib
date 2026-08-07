package net.creeperhost.polylib.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** Client-only channel negotiation access kept out of the service loaded by dedicated servers. */
final class NeoForgeClientNetworkAccess
{
    static boolean canSend(CustomPacketPayload.Type<?> type)
    {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && NetworkRegistry.hasChannel(connection, type.id());
    }

    private NeoForgeClientNetworkAccess()
    {
    }
}
