package net.creeperhost.polylib;

import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

/**
 * NeoForge server-side game-bus event handlers for PolyLib lifecycle hooks.
 */
@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeEvents
{
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PlayerClientSettingsManager.onPlayerLogin(sp);
            PlayerServerDataManager.onPlayerLogin(sp);
            PolyPlayerEvents.LOGIN.invoker().onLogin(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            UUID uuid = sp.getUUID();
            AccessibilityPrefsManager.clearPlayer(uuid);
            PlayerClientSettingsManager.onPlayerLogout(uuid);
            PlayerServerDataManager.onPlayerLogout(uuid);
            PolyPlayerEvents.LOGOUT.invoker().onLogout(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PlayerClientSettingsManager.onPlayerRespawn(sp.getUUID(), sp);
            PlayerServerDataManager.onPlayerRespawn(sp.getUUID(), sp);
            PolyPlayerEvents.RESPAWN.invoker().onRespawn(sp, event.isEndConquered());
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof ServerPlayer tracked
                && event.getEntity() instanceof ServerPlayer tracker)
        {
            PlayerClientSettingsManager.syncTrackingRange(tracked, tracker);
            PolyPlayerEvents.START_TRACKING.invoker().onStartTracking(tracked, tracker);
        }
    }
}
