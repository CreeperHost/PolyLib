package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Server-side explosion events for start, detonation, and entity knockback.
 */
public final class PolyExplosionEvents
{
    /**
     * Fired when an explosion starts, before any blocks or entities are affected.
     * Call {@link CancelContext#cancel()} to prevent the explosion.
     * <p>
     * NeoForge: {@code ExplosionEvent.Start} (cancellable)<br>
     * Fabric: no native equivalent — NeoForge-only cancel support
     */
    public static final PolyEvent<Start> START = PolyEvent.create(handlers -> (level, ctx) ->
    {
        for (var h : handlers)
        {
            h.onStart(level, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after the explosion has calculated the list of affected blocks and entities.
     * Handlers may read (or via NeoForge modify) the affected lists.
     * <p>
     * NeoForge: {@code ExplosionEvent.Detonate}<br>
     * Fabric: no native equivalent
     */
    public static final PolyEvent<Detonate> DETONATE = PolyEvent.create(
            handlers -> (level, blocks, entities) -> handlers.forEach(h -> h.onDetonate(level, blocks, entities)));

    /**
     * Fired when explosion knockback velocity is being applied to an entity.
     * <p>
     * NeoForge: {@code ExplosionKnockbackEvent}<br>
     * Fabric: no native equivalent
     */
    public static final PolyEvent<Knockback> KNOCKBACK = PolyEvent.create(
            handlers -> (level, entity, velocity) -> handlers.forEach(h -> h.onKnockback(level, entity, velocity)));

    private PolyExplosionEvents()
    {
    }

    @FunctionalInterface
    public interface Start
    {
        void onStart(Level level, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Detonate
    {
        void onDetonate(Level level, List<BlockPos> affectedBlocks, List<Entity> affectedEntities);
    }

    @FunctionalInterface
    public interface Knockback
    {
        void onKnockback(Level level, Entity entity, Vec3 velocity);
    }
}
