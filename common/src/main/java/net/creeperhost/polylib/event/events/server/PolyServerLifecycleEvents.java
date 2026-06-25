package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Set;

/**
 * Server lifecycle events for startup, shutdown, reloads, datapack sync,
 * login/configuration negotiation, game tests, and reload listener ordering.
 */
public final class PolyServerLifecycleEvents
{
    public static final PolyEvent<Started> SERVER_STARTED = PolyEvent.create(handlers -> server -> handlers.forEach(h -> h.onStarted(server)));
    public static final PolyEvent<Stopping> SERVER_STOPPING = PolyEvent.create(handlers -> server -> handlers.forEach(h -> h.onStopping(server)));
    public static final PolyEvent<Stopped> SERVER_STOPPED = PolyEvent.create(handlers -> server -> handlers.forEach(h -> h.onStopped(server)));


    /**
     * Fired when data packs sync to a player (or all players on login/reload).
     * {@code player} is null when syncing to all connected players.
     * <p>
     * NeoForge: {@code OnDatapackSyncEvent} (GAME bus)<br>
     * Fabric: mixin on {@code PlayerList#reloadResources} / {@code ServerPlayer#sendServerStatus}
     */
    public static final PolyEvent<DatapackSync> DATAPACK_SYNC = PolyEvent.create(
            handlers -> (server, player) -> handlers.forEach(h -> h.onDatapackSync(server, player)));

    /**
     * Fired when server data pack reloading begins.
     * <p>
     * NeoForge: mixin on {@code MinecraftServer#reloadResources}<br>
     * Fabric: {@code ServerLifecycleEvents.START_DATA_PACK_RELOAD}
     */
    public static final PolyEvent<ReloadStart> RELOAD_START = PolyEvent.create(
            handlers -> (server, rm) -> handlers.forEach(h -> h.onReloadStart(server, rm)));

    /**
     * Fired when server data pack reloading finishes.
     * <p>
     * NeoForge: mixin on {@code MinecraftServer#reloadResources}<br>
     * Fabric: {@code ServerLifecycleEvents.END_DATA_PACK_RELOAD}
     */
    public static final PolyEvent<ReloadEnd> RELOAD_END = PolyEvent.create(
            handlers -> (server, rm, success) -> handlers.forEach(h -> h.onReloadEnd(server, rm, success)));


    /**
     * Fires after all server levels have been fully set up.
     * On NeoForge this maps to {@code ServerStartedEvent}.
     * On Fabric this maps to {@code ServerLifecycleEvents.AFTER_SETUP}.
     */
    public static final PolyEvent<AfterSetup> SERVER_AFTER_SETUP = PolyEvent.create(
            handlers -> server -> handlers.forEach(h -> h.onServerAfterSetup(server)));

    /**
     * Fires on world load when one or more mod versions differ from versions stored in the save.
     * NeoForge-only — Fabric fires nothing.
     */
    public static final PolyEvent<ModMismatch> MOD_MISMATCH = PolyEvent.create(
            handlers -> (mismatchedModIds, anyUnresolved) ->
                    handlers.forEach(h -> h.onModMismatch(mismatchedModIds, anyUnresolved)));

    /**
     * Fires on the NeoForge Mod Bus during setup to register game test functions.
     * NeoForge-only — Fabric uses annotation-based registration.
     */
    public static final PolyEvent<RegisterGameTests> REGISTER_GAME_TESTS = PolyEvent.create(
            handlers -> event -> handlers.forEach(h -> h.onRegisterGameTests(event)));

    /**
     * Fires on the NeoForge Mod Bus during setup to register legacy structure NBT conversions.
     * NeoForge-only — Fabric has no equivalent.
     */
    public static final PolyEvent<RegisterStructureConversions> REGISTER_STRUCTURE_CONVERSIONS = PolyEvent.create(
            handlers -> event -> handlers.forEach(h -> h.onRegisterStructureConversions(event)));

    /**
     * Fires during the server login query phase, before authentication completes.
     * {@code sender} and {@code syncCallback} are Fabric-specific; NeoForge provides null.
     */
    public static final PolyEvent<LoginQueryStart> LOGIN_QUERY_START = PolyEvent.create(
            handlers -> (listener, server, sender, syncCallback) ->
                    handlers.forEach(h -> h.onLoginQueryStart(listener, server, sender, syncCallback)));

    /**
     * Fires when a client disconnects during the server configuration phase.
     * Fabric-native; NeoForge uses a mixin bridge.
     */
    public static final PolyEvent<ConfigurationDisconnect> CONFIGURATION_DISCONNECT = PolyEvent.create(
            handlers -> (listener, server) ->
                    handlers.forEach(h -> h.onConfigurationDisconnect(listener, server)));

    /**
     * Fires during NeoForge channel negotiation. NeoForge-only.
     */
    public static final PolyEvent<PlayerNegotiation> PLAYER_NEGOTIATION = PolyEvent.create(
            handlers -> connection -> handlers.forEach(h -> h.onPlayerNegotiation(connection)));


    /**
     * Fires on the NeoForge Mod Bus when resource reload listeners are being sorted.
     * Handlers receive the raw {@code SortedReloadListenerEvent} as an {@code Object} to avoid
     * coupling common code to NeoForge API.
     * <p>
     * NeoForge-only — Fabric fires nothing (no-op).
     */
    public static final PolyEvent<SortReloadListeners> SORT_RELOAD_LISTENERS = PolyEvent.create(
            handlers -> event -> handlers.forEach(h -> h.onSortReloadListeners(event)));

    private PolyServerLifecycleEvents() {}

    @FunctionalInterface
    public interface Started
    {
        void onStarted(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopping
    {
        void onStopping(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopped
    {
        void onStopped(MinecraftServer server);
    }

    @FunctionalInterface
    public interface DatapackSync
    {
        /** {@code player} is null when syncing to all players. */
        void onDatapackSync(MinecraftServer server, ServerPlayer player);
    }

    @FunctionalInterface
    public interface ReloadStart
    {
        void onReloadStart(MinecraftServer server, ResourceManager rm);
    }

    @FunctionalInterface
    public interface ReloadEnd
    {
        void onReloadEnd(MinecraftServer server, ResourceManager rm, boolean success);
    }


    @FunctionalInterface
    public interface AfterSetup
    {
        void onServerAfterSetup(MinecraftServer server);
    }

    @FunctionalInterface
    public interface ModMismatch
    {
        /**
         * @param mismatchedModIds Set of mod IDs whose saved versions differ from current versions.
         * @param anyUnresolved    True if any mismatches remain unresolved after handler runs.
         */
        void onModMismatch(Set<String> mismatchedModIds, boolean anyUnresolved);
    }

    @FunctionalInterface
    public interface RegisterGameTests
    {
        /**
         * Use the raw NeoForge event to register test classes/instances annotated with {@code @GameTest}.
         * The {@code event} is typed as {@code Object} to avoid coupling common code to NeoForge API.
         */
        void onRegisterGameTests(Object event);
    }

    @FunctionalInterface
    public interface RegisterStructureConversions
    {
        /**
         * Use the raw NeoForge event to register legacy structure ID remappings.
         * The {@code event} is typed as {@code Object} to avoid coupling common code to NeoForge API.
         */
        void onRegisterStructureConversions(Object event);
    }

    @FunctionalInterface
    public interface LoginQueryStart
    {
        /**
         * @param listener     The login connection handler.
         * @param server       The server instance.
         * @param sender       Packet sender (Fabric: {@code PacketSender}; NeoForge: null).
         * @param syncCallback Login sync callback (Fabric: {@code LoginSyncCallback}; NeoForge: null).
         */
        void onLoginQueryStart(ServerLoginPacketListenerImpl listener, MinecraftServer server,
                               Object sender, Object syncCallback);
    }

    @FunctionalInterface
    public interface ConfigurationDisconnect
    {
        void onConfigurationDisconnect(ServerConfigurationPacketListenerImpl listener, MinecraftServer server);
    }

    @FunctionalInterface
    public interface PlayerNegotiation
    {
        void onPlayerNegotiation(Connection connection);
    }


    @FunctionalInterface
    public interface SortReloadListeners
    {
        /**
         * @param event On NeoForge: {@code SortedReloadListenerEvent};
         *              on Fabric: always null (event never fires).
         */
        void onSortReloadListeners(Object event);
    }
}
