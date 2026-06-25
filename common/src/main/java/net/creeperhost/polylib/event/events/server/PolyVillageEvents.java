package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Events related to villages, villager trading, and sieges.
 * <p>
 * NeoForge: {@code VillageSiegeEvent}<br>
 * Fabric: mixin into {@code VillageSiege#tick}
 */
public final class PolyVillageEvents
{

    /**
     * Fired when the game attempts to spawn a zombie siege in a village.
     * Call {@link CancelContext#cancel()} to prevent the siege from spawning.
     * <p>
     * NeoForge: {@code VillageSiegeEvent} (cancellable)<br>
     * Fabric: mixin into {@code VillageSiege#tick}
     */
    public static final PolyEvent<SiegeSpawn> SIEGE_SPAWN = PolyEvent.create(handlers -> (siege, level, player, attemptedPos, ctx) ->
    {
        for (var h : handlers)
        {
            h.onSiegeSpawn(siege, level, player, attemptedPos, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    private PolyVillageEvents() {}


    @FunctionalInterface
    public interface SiegeSpawn
    {
        void onSiegeSpawn(VillageSiege siege, Level level, Player player, Vec3 attemptedPos, CancelContext ctx);
    }
}
