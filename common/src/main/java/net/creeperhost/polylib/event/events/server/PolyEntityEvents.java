package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Server-side entity events for ticking, level changes, mounting, teleporting,
 * projectile impacts, mob griefing, and invulnerability checks.
 */
public final class PolyEntityEvents
{
    /**
     * Fired every tick for each entity that is ticking (server-side).
     * Equivalent to NeoForge {@code EntityTickEvent.Post}.
     */
    public static final PolyEvent<EntityTick> ENTITY_TICK = PolyEvent.create(handlers -> entity -> handlers.forEach(h -> h.onEntityTick(entity)));


    /**
     * Fired after an entity's tick method completes. Completes the entity tick pair.
     * <p>
     * NeoForge: {@code EntityTickEvent.Post}<br>
     * Fabric: mixin on {@code Entity#tick} at RETURN
     */
    public static final PolyEvent<EntityTickEnd> ENTITY_TICK_END = PolyEvent.create(
            handlers -> entity -> handlers.forEach(h -> h.onEntityTickEnd(entity)));

    /**
     * Fired when an entity is about to join a server level.
     * Call {@link CancelContext#cancel()} to prevent the entity from joining.
     * Equivalent to NeoForge {@code EntityJoinLevelEvent}.
     * <p>Note: cancellation is not supported on Fabric via the direct lifecycle event.
     */
    public static final PolyEvent<JoinLevel> JOIN_LEVEL = PolyEvent.create(handlers -> (entity, level, ctx) ->
    {
        for (var h : handlers)
        {
            h.onJoinLevel(entity, level, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when an entity is about to mount or dismount a vehicle.
     * Call {@link CancelContext#cancel()} to prevent the mount/dismount.
     * <p>
     * NeoForge: {@code EntityMountEvent} (cancellable)<br>
     * Fabric: mixin into {@code Entity#startRiding}
     */
    public static final PolyEvent<Mount> MOUNT = PolyEvent.create(handlers -> (entity, vehicle, isMounting, ctx) ->
    {
        for (var h : handlers)
        {
            h.onMount(entity, vehicle, isMounting, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when an entity leaves a server level (is unloaded or removed).
     * Informational — not cancellable.
     * <p>
     * NeoForge: {@code EntityLeaveLevelEvent}<br>
     * Fabric: {@code ServerEntityEvents.ENTITY_UNLOAD}
     */
    public static final PolyEvent<LeaveLevel> LEAVE_LEVEL = PolyEvent.create(
            handlers -> (entity, level) -> handlers.forEach(h -> h.onLeaveLevel(entity, level)));

    /**
     * Fired when an entity is about to teleport (within the same level).
     * Call {@link CancelContext#cancel()} to prevent the teleport.
     * <p>
     * NeoForge: {@code EntityTeleportEvent}<br>
     * Fabric: mixin into {@code Entity#teleportTo}
     */
    public static final PolyEvent<Teleport> TELEPORT = PolyEvent.create(handlers -> (entity, target, ctx) ->
    {
        for (var h : handlers)
        {
            h.onTeleport(entity, target, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when an entity is about to travel to a different dimension.
     * Call {@link CancelContext#cancel()} to prevent the dimension change.
     * <p>
     * Note: cancellation is not supported on Fabric (fires post-change via
     * {@code ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_WORLD}).
     * <p>
     * NeoForge: {@code EntityTravelToDimensionEvent} (cancellable)
     */
    public static final PolyEvent<TravelDimension> TRAVEL_DIMENSION = PolyEvent.create(handlers -> (entity, destination, ctx) ->
    {
        for (var h : handlers)
        {
            h.onTravelDimension(entity, destination, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when the game checks whether a mob is allowed to grief (break blocks, etc.).
     * Set the result via {@link GriefingContext#setCanGrief(boolean)}.
     * <p>
     * NeoForge: {@code EntityMobGriefingEvent}<br>
     * Fabric: no direct equivalent (TODO: mixin)
     */
    public static final PolyEvent<MobGriefing> MOB_GRIEFING = PolyEvent.create(
            handlers -> (entity, ctx) -> handlers.forEach(h -> h.onMobGriefing(entity, ctx)));

    /**
     * Fired when a projectile impacts a block or entity.
     * Call {@link CancelContext#cancel()} to prevent processing the impact.
     * <p>
     * NeoForge: {@code ProjectileImpactEvent}<br>
     * Fabric: mixin into {@code Projectile#hitTargetOrDeflectSelf}
     */
    public static final PolyEvent<ProjectileImpact> PROJECTILE_IMPACT = PolyEvent.create(handlers -> (projectile, hitResult, ctx) ->
    {
        for (var h : handlers)
        {
            h.onProjectileImpact(projectile, hitResult, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when an entity is about to be struck by a lightning bolt.
     * Call {@link CancelContext#cancel()} to prevent the strike.
     * <p>
     * NeoForge: {@code EntityStruckByLightningEvent}<br>
     * Fabric: mixin into {@code Entity#thunderHit}
     */
    public static final PolyEvent<StruckByLightning> STRUCK_BY_LIGHTNING = PolyEvent.create(handlers -> (entity, lightning, ctx) ->
    {
        for (var h : handlers)
        {
            h.onStruckByLightning(entity, lightning, ctx);
            if (ctx.isCancelled()) break;
        }
    });


    /**
     * Fires when the engine checks whether an entity is invulnerable to a damage source.
     * Each handler receives the accumulated result from prior handlers and may flip it.
     * <p>
     * NeoForge: {@code EntityInvulnerabilityCheckEvent}<br>
     * Fabric: mixin on {@code Entity#isInvulnerableTo} at RETURN
     */
    public static final PolyEvent<InvulnerabilityCheck> ENTITY_INVULNERABILITY_CHECK = PolyEvent.create(
            handlers -> (entity, source, originalResult) ->
            {
                boolean result = originalResult;
                for (var h : handlers) result = h.onInvulnerabilityCheck(entity, source, result);
                return result;
            });

    /**
     * Observer fired after a non-player entity has fully completed a dimension change.
     * {@code original} is the entity that entered the portal; {@code replacement} is the new
     * instance in the destination level. Player dimension changes are covered by
     * {@code PolyPlayerEvents.CHANGE_DIMENSION}.
     * <p>
     * NeoForge: mixin on {@code Entity#changeDimension} at RETURN<br>
     * Fabric: {@code ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL}
     */
    public static final PolyEvent<EntityChangeLevelPost> ENTITY_CHANGE_LEVEL_POST = PolyEvent.create(
            handlers -> (original, replacement, from, to) ->
                    handlers.forEach(h -> h.onEntityChangeLevelPost(original, replacement, from, to)));

    private PolyEntityEvents() {}

    @FunctionalInterface
    public interface EntityTick
    {
        void onEntityTick(Entity entity);
    }

    @FunctionalInterface
    public interface JoinLevel
    {
        void onJoinLevel(Entity entity, ServerLevel level, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Mount
    {
        void onMount(Entity entity, Entity vehicle, boolean isMounting, CancelContext ctx);
    }

    @FunctionalInterface
    public interface LeaveLevel
    {
        void onLeaveLevel(Entity entity, ServerLevel level);
    }

    @FunctionalInterface
    public interface Teleport
    {
        /**
         * @param target mutable {@code double[3]} array containing the target {x, y, z}.
         *               Modify in-place to redirect the teleport destination.
         */
        void onTeleport(Entity entity, double[] target, CancelContext ctx);
    }

    @FunctionalInterface
    public interface TravelDimension
    {
        void onTravelDimension(Entity entity, ResourceKey<Level> destination, CancelContext ctx);
    }

    @FunctionalInterface
    public interface MobGriefing
    {
        void onMobGriefing(Entity entity, GriefingContext ctx);
    }

    @FunctionalInterface
    public interface ProjectileImpact
    {
        void onProjectileImpact(Projectile projectile, HitResult hitResult, CancelContext ctx);
    }

    @FunctionalInterface
    public interface StruckByLightning
    {
        void onStruckByLightning(Entity entity, LightningBolt lightning, CancelContext ctx);
    }

    /** Mutable result context for mob-griefing checks. */
    public static final class GriefingContext
    {
        private boolean canGrief;

        public GriefingContext(boolean defaultValue) { this.canGrief = defaultValue; }

        public boolean canGrief() { return canGrief; }

        public void setCanGrief(boolean value) { this.canGrief = value; }
    }


    @FunctionalInterface
    public interface EntityTickEnd
    {
        void onEntityTickEnd(Entity entity);
    }


    @FunctionalInterface
    public interface InvulnerabilityCheck
    {
        /**
         * @return {@code true} = entity is invulnerable (absorbs hit),
         *         {@code false} = entity is vulnerable (takes hit)
         */
        boolean onInvulnerabilityCheck(Entity entity, DamageSource source, boolean originalInvulnerable);
    }

    @FunctionalInterface
    public interface EntityChangeLevelPost
    {
        void onEntityChangeLevelPost(Entity original, Entity replacement, ServerLevel from, ServerLevel to);
    }
}
