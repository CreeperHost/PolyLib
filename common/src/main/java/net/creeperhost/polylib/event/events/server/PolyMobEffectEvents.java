package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class PolyMobEffectEvents
{
    /**
     * Fired to check whether a mob effect may be applied to an entity.
     * Call {@link CancelContext#cancel()} to prevent application.
     * <p>
     * NeoForge: {@code MobEffectEvent.Applicable} (Result.DO_NOT_APPLY)<br>
     * Fabric: {@code ServerMobEffectEvents.ALLOW_ADD} (boolean gate)
     */
    public static final PolyEvent<AllowApply> ALLOW_APPLY = PolyEvent.create(handlers -> (entity, effect, ctx) ->
    {
        for (var h : handlers)
        {
            h.onAllowApply(entity, effect, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after a mob effect has been applied.
     * <p>
     * NeoForge: {@code MobEffectEvent.Added}<br>
     * Fabric: {@code ServerMobEffectEvents.AFTER_ADD}
     */
    public static final PolyEvent<Applied> APPLIED = PolyEvent.create(
            handlers -> (entity, effect) -> handlers.forEach(h -> h.onApplied(entity, effect)));

    /**
     * Fired when a mob effect is being removed.
     * <p>
     * NeoForge: {@code MobEffectEvent.Remove}<br>
     * Fabric: {@code ServerMobEffectEvents.BEFORE_REMOVE}
     */
    public static final PolyEvent<Removed> REMOVED = PolyEvent.create(
            handlers -> (entity, effect) -> handlers.forEach(h -> h.onRemoved(entity, effect)));

    /**
     * Fired when a mob effect expires naturally (duration runs out).
     * <p>
     * NeoForge: {@code MobEffectEvent.Expired}<br>
     * Fabric: no native equivalent — requires a mixin bridge (TODO)
     */
    public static final PolyEvent<Expired> EXPIRED = PolyEvent.create(
            handlers -> (entity, effect) -> handlers.forEach(h -> h.onExpired(entity, effect)));

    private PolyMobEffectEvents()
    {
    }

    @FunctionalInterface
    public interface AllowApply
    {
        void onAllowApply(LivingEntity entity, MobEffectInstance effect, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Applied
    {
        void onApplied(LivingEntity entity, MobEffectInstance effect);
    }

    @FunctionalInterface
    public interface Removed
    {
        void onRemoved(LivingEntity entity, MobEffectInstance effect);
    }

    @FunctionalInterface
    public interface Expired
    {
        void onExpired(LivingEntity entity, MobEffectInstance effect);
    }
}
