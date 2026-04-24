package net.creeperhost.polylib;

import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.ModMismatchEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * NeoForge Mod Bus event handlers for PolyLib Tier 15 mod-bus-only events.
 *
 * <p>In NeoForge 26.1.x the {@code EventBusSubscriber.Bus} enum was removed.
 * These handlers are registered manually via {@code eventBus.addListener()} in
 * {@link PolyLibNeoForge}.
 */
public final class NeoForgeModBusEvents
{
    private NeoForgeModBusEvents() {}

    public static void register(IEventBus eventBus)
    {
        eventBus.addListener(NeoForgeModBusEvents::onModMismatch);
        eventBus.addListener(NeoForgeModBusEvents::onRegisterGameTests);
        // AddServerReloadListenersEvent is a game-bus event (concrete subclass of abstract SortedReloadListenerEvent)
        NeoForge.EVENT_BUS.addListener(NeoForgeModBusEvents::onSortReloadListeners);
    }

    private static void onModMismatch(ModMismatchEvent event)
    {
        Set<String> ids = new HashSet<>();
        event.getUnresolved()
                .map(ModMismatchEvent.MismatchResolutionResult::modid)
                .forEach(ids::add);
        PolyServerLifecycleEvents.MOD_MISMATCH.invoker()
                .onModMismatch(ids, event.anyUnresolved());
    }

    private static void onRegisterGameTests(RegisterGameTestsEvent event)
    {
        PolyServerLifecycleEvents.REGISTER_GAME_TESTS.invoker().onRegisterGameTests(event);
    }

    // ── Tier 22 ───────────────────────────────────────────────────────────────

    private static void onSortReloadListeners(AddServerReloadListenersEvent event)
    {
        PolyServerLifecycleEvents.SORT_RELOAD_LISTENERS.invoker().onSortReloadListeners(event);
    }
}
