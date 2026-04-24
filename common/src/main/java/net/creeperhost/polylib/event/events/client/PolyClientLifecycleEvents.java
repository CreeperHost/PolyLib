package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;

public final class PolyClientLifecycleEvents
{
    /**
     * Fired once when the Minecraft client has fully started and is entering its main game loop.
     * <p>
     * NeoForge: mixin {@code MixinMinecraftRun} on {@code Minecraft#run} at HEAD (fire-once guard).<br>
     * Fabric: {@code ClientLifecycleEvents.CLIENT_STARTED}.
     */
    public static final PolyEvent<ClientStarted> CLIENT_STARTED = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onClientStarted(mc)));

    /**
     * Fired when the Minecraft client is shutting down.
     * <p>
     * NeoForge: {@code GameShuttingDownEvent}.<br>
     * Fabric: {@code ClientLifecycleEvents.CLIENT_STOPPING}.
     */
    public static final PolyEvent<ClientStopping> CLIENT_STOPPING = PolyEvent.create(
            handlers -> mc -> handlers.forEach(h -> h.onClientStopping(mc)));

    private PolyClientLifecycleEvents() {}

    @FunctionalInterface
    public interface ClientStarted
    {
        void onClientStarted(Minecraft client);
    }

    @FunctionalInterface
    public interface ClientStopping
    {
        void onClientStopping(Minecraft client);
    }

    // ── Tier 17-C ─────────────────────────────────────────────────────────────

    /**
     * Fired after the client's active level reference changes — on join, dimension
     * change, or disconnect. {@code level} is the new {@link ClientLevel}, or
     * {@code null} when the level has been removed (e.g., disconnect).
     * <p>
     * NeoForge: mixin on {@code Minecraft#setLevel} / {@code Minecraft#clearClientLevel} at TAIL<br>
     * Fabric: {@code ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE}
     */
    public static final PolyEvent<ClientLevelChanged> CLIENT_LEVEL_CHANGED = PolyEvent.create(
            handlers -> (client, level) -> handlers.forEach(h -> h.onClientLevelChanged(client, level)));

    @FunctionalInterface
    public interface ClientLevelChanged
    {
        /** @param level the new {@link ClientLevel}, or {@code null} when disconnecting. */
        void onClientLevelChanged(Minecraft client, @Nullable ClientLevel level);
    }
}
