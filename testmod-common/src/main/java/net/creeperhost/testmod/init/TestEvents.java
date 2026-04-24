package net.creeperhost.testmod.init;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.testmod.TestModCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestEvents
{

    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        PolyPlayerEvents.LOGIN.register(player -> LOGGER.info("[TestMod] {} joined. Ticks played: {}", player.getScoreboardName(), PlayerServerDataManager.get(player, TestModCommon.TICKS_PLAYED)));

        PolyPlayerEvents.LOGOUT.register(player -> LOGGER.info("[TestMod] {} left the server.", player.getScoreboardName()));

        PolyPlayerEvents.RESPAWN.register((player, endConquered) -> LOGGER.info("[TestMod] {} respawned (endConquered={}).", player.getScoreboardName(), endConquered));

        PolyPlayerEvents.START_TRACKING.register((tracked, tracker) -> LOGGER.info("[TestMod] {} is now tracking {}.", tracker.getScoreboardName(), tracked.getScoreboardName()));
    }
}
