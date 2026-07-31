package net.creeperhost.testmod.network;

import net.creeperhost.polylib.network.OptionalPackets;
import net.creeperhost.testmod.TestModCommon;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Registers and handles the testmod optional packet round trip.
 */
public final class TestOptionalPackets
{
    private static final String DISABLED_PROPERTY = "testmod.optionalPackets.disabled";

    private TestOptionalPackets() {}

    /**
     * Registers packet codecs and the serverbound handler from common initialization.
     */
    public static void init()
    {
        if (isDisabled())
        {
            TestModCommon.LOGGER.info("Optional test packet registration disabled by {}", DISABLED_PROPERTY);
            return;
        }

        OptionalPackets.registerClientbound(OptionalPongPayload.TYPE, OptionalPongPayload.CODEC);
        OptionalPackets.registerServerbound(OptionalPingPayload.TYPE, OptionalPingPayload.CODEC, (payload, player) ->
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                boolean replied = OptionalPackets.sendToPlayer(serverPlayer, new OptionalPongPayload(payload.value()));
                TestModCommon.LOGGER.info("Optional ping {} received from {}; replied={}",
                        payload.value(), player.getScoreboardName(), replied);
            }
        });
    }

    /**
     * Registers the clientbound handler from physical-client initialization.
     */
    public static void initClient()
    {
        if (isDisabled()) return;

        OptionalPackets.registerClientHandler(OptionalPongPayload.TYPE, (payload, player) ->
        {
            player.sendSystemMessage(Component.literal("Optional packet pong: " + payload.value()));
            TestModCommon.LOGGER.info("Optional pong {} received from server", payload.value());
        });
    }

    /**
     * Returns whether this process should omit the optional test packet channel.
     *
     * <p>The testmod client runners set this property when launched with
     * {@code -PwithoutOptionalPackets}, allowing compatibility testing against a
     * server that still registers the packets.</p>
     *
     * @return {@code true} when optional test packet registration is disabled
     */
    private static boolean isDisabled()
    {
        return Boolean.getBoolean(DISABLED_PROPERTY);
    }
}
