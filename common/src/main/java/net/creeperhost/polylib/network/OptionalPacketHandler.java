package net.creeperhost.polylib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Handles an optional play-stage packet on the main game thread.
 *
 * @param <T> payload type handled by this callback
 */
@FunctionalInterface
public interface OptionalPacketHandler<T extends CustomPacketPayload>
{
    /**
     * Handles a decoded payload.
     * <p>
     * Serverbound handlers receive a {@code ServerPlayer}; clientbound handlers
     * receive the local client player.
     *
     * @param payload decoded payload
     * @param player  player associated with the receiving connection
     */
    void handle(T payload, Player player);
}
