package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Events related to mob spawning and despawning.
 * <p>
 * NeoForge: {@code FinalizeSpawnEvent}, {@code MobDespawnEvent}<br>
 * Fabric: mixins into {@code Mob#finalizeSpawn} and {@code Mob#checkDespawn}
 */
public final class PolySpawnEvents
{
    /**
     * Fired before a mob's spawn is finalized (equipment/attribute assignment).
     * Call {@link CancelContext#cancel()} to prevent {@code finalizeSpawn} from running.
     * <p>
     * NeoForge: {@code FinalizeSpawnEvent} (cancellable)<br>
     * Fabric: mixin into {@code Mob#finalizeSpawn}
     */
    public static final PolyEvent<FinalizeSpawn> FINALIZE_SPAWN = PolyEvent.create(handlers -> (mob, level, ctx) ->
    {
        for (var h : handlers)
        {
            h.onFinalizeSpawn(mob, level, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a mob is about to check if it should despawn.
     * Use {@link DespawnContext} to force keep or force remove.
     * <p>
     * NeoForge: {@code MobDespawnEvent} (result-based)<br>
     * Fabric: mixin into {@code Mob#checkDespawn}
     */
    public static final PolyEvent<MobDespawn> MOB_DESPAWN = PolyEvent.create(
            handlers -> (mob, ctx) -> handlers.forEach(h -> h.onMobDespawn(mob, ctx)));


    /**
     * Fires immediately when a mob enters the world (before finalizeSpawn / equipment assignment).
     * Not cancellable. {@code spawnType} and {@code difficulty} may be null on some loaders.
     * <p>
     * NeoForge: {@code MobSpawnEvent} (base class)<br>
     * Fabric: mixin into {@code Mob#finalizeSpawn} at HEAD
     */
    public static final PolyEvent<MobSpawnContext> MOB_SPAWN_CONTEXT = PolyEvent.create(
            handlers -> (mob, level, pos, spawnType, difficulty) ->
                    handlers.forEach(h -> h.onMobSpawnContext(mob, level, pos, spawnType, difficulty)));

    private PolySpawnEvents() {}

    @FunctionalInterface
    public interface FinalizeSpawn
    {
        void onFinalizeSpawn(Mob mob, ServerLevelAccessor level, CancelContext ctx);
    }

    @FunctionalInterface
    public interface MobDespawn
    {
        void onMobDespawn(Mob mob, DespawnContext ctx);
    }


    @FunctionalInterface
    public interface MobSpawnContext
    {
        /**
         * @param spawnType  May be null when invoked from base {@code MobSpawnEvent} on NeoForge.
         * @param difficulty May be null (not available on all spawn event classes).
         */
        void onMobSpawnContext(Mob mob, ServerLevelAccessor level, BlockPos pos,
                               EntitySpawnReason spawnType,
                               net.minecraft.world.DifficultyInstance difficulty);
    }

    /** Result context for despawn decisions. */
    public static final class DespawnContext
    {
        public enum Result { DEFAULT, KEEP, REMOVE }

        private Result result = Result.DEFAULT;

        public Result getResult() { return result; }

        /** Prevent the mob from despawning this tick. */
        public void keepAlive() { result = Result.KEEP; }

        /** Force the mob to despawn immediately. */
        public void forceRemove() { result = Result.REMOVE; }
    }
}
