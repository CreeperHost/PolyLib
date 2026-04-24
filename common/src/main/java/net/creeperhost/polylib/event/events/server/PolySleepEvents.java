package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Events related to entity sleeping behaviour.
 * <p>
 * NeoForge: {@code CanPlayerSleepEvent}; start/stop via mixin<br>
 * Fabric: {@code EntitySleepEvents.START_SLEEPING}, {@code STOP_SLEEPING}, {@code ALLOW_SLEEPING}
 */
public final class PolySleepEvents
{
    /**
     * Fired when a living entity starts sleeping (entering a bed).
     * Informational — not cancellable.
     * <p>
     * NeoForge: mixin into {@code LivingEntity#startSleeping}<br>
     * Fabric: {@code EntitySleepEvents.START_SLEEPING}
     */
    public static final PolyEvent<StartSleeping> START_SLEEPING = PolyEvent.create(
            handlers -> (entity, pos) -> handlers.forEach(h -> h.onStartSleeping(entity, pos)));

    /**
     * Fired when a living entity stops sleeping (waking up).
     * Informational — not cancellable. {@code pos} may be null if the bed was destroyed.
     * <p>
     * NeoForge: mixin into {@code LivingEntity#stopSleeping}<br>
     * Fabric: {@code EntitySleepEvents.STOP_SLEEPING}
     */
    public static final PolyEvent<StopSleeping> STOP_SLEEPING = PolyEvent.create(
            handlers -> (entity, pos) -> handlers.forEach(h -> h.onStopSleeping(entity, pos)));

    /**
     * Fired when the game checks if a player is allowed to sleep.
     * Use {@link SleepContext#setProblem} to prevent sleeping with a reason.
     * <p>
     * NeoForge: {@code CanPlayerSleepEvent}<br>
     * Fabric: {@code EntitySleepEvents.ALLOW_SLEEPING}
     */
    public static final PolyEvent<AllowSleeping> ALLOW_SLEEPING = PolyEvent.create(
            handlers -> (player, pos, ctx) -> handlers.forEach(h -> h.onAllowSleeping(player, pos, ctx)));

    private PolySleepEvents() {}

    @FunctionalInterface
    public interface StartSleeping
    {
        void onStartSleeping(LivingEntity entity, BlockPos pos);
    }

    @FunctionalInterface
    public interface StopSleeping
    {
        void onStopSleeping(LivingEntity entity, BlockPos pos);
    }

    @FunctionalInterface
    public interface AllowSleeping
    {
        /** Set {@link SleepContext#setProblem} to a non-null value to block sleeping with a reason. */
        void onAllowSleeping(net.minecraft.server.level.ServerPlayer player, BlockPos pos, SleepContext ctx);
    }

    /** Result context for sleep-permission checks. */
    public static final class SleepContext
    {
        private Player.BedSleepingProblem problem;

        /** @param vanillaProblem the vanilla problem, or {@code null} if no issue exists */
        public SleepContext(Player.BedSleepingProblem vanillaProblem)
        {
            this.problem = vanillaProblem;
        }

        /** Returns the current problem, or {@code null} if sleeping is allowed. */
        public Player.BedSleepingProblem getProblem() { return problem; }

        /** Set to a non-null problem to prevent sleeping. Set to {@code null} to allow. */
        public void setProblem(Player.BedSleepingProblem p) { problem = p; }

        public boolean isSleepAllowed() { return problem == null; }
    }
}
