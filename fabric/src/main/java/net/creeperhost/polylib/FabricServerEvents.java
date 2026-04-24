package net.creeperhost.polylib;

// TODO: depends on feat/accessibility PR being merged — AccessibilityPrefsManager lives there
import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

/**
 * Fabric server-side lifecycle hooks for PolyLib player settings.
 * Registered in {@link PolyLibFabric#onInitialize()}.
 * Respawn/death copy is handled automatically by the {@code copyOnDeath()} flag on each AttachmentType.
 */
public final class FabricServerEvents
{
    private FabricServerEvents() {}

    public static void register()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
            ServerPlayer player = handler.getPlayer();
            PlayerClientSettingsManager.onPlayerLogin(player);
            PlayerServerDataManager.onPlayerLogin(player);
            PolyPlayerEvents.LOGIN.invoker().onLogin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            ServerPlayer player = handler.getPlayer();
            UUID uuid = player.getUUID();
            AccessibilityPrefsManager.clearPlayer(uuid);
            PlayerClientSettingsManager.onPlayerLogout(uuid);
            PlayerServerDataManager.onPlayerLogout(uuid);
            PolyPlayerEvents.LOGOUT.invoker().onLogout(player);
        });
    }
}
