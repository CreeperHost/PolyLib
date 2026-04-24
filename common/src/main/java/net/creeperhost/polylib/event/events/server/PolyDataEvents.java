package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

/**
 * Events related to data/resource loading.
 * <p>
 * Note: {@code REGISTER_COMMANDS} is located in {@link PolyServerCommandEvents}.
 */
public final class PolyDataEvents
{
    /**
     * Fired after tags are loaded or reloaded on the server (startup and {@code /reload}).
     * <p>
     * NeoForge: {@code TagsUpdatedEvent} (SERVER_DATA_LOAD cause only)<br>
     * Fabric: no direct equivalent — NeoForge only initially (TODO: Fabric bridge)
     */
    public static final PolyEvent<TagsUpdated> TAGS_UPDATED = PolyEvent.create(
            handlers -> provider -> handlers.forEach(h -> h.onTagsUpdated(provider)));

    /**
     * Fired when a loot table is being loaded from data packs.
     * <p>
     * NeoForge: {@code LootTableLoadEvent} (game bus)<br>
     * Fabric: mixin on {@code LootDataManager} loading path
     */
    public static final PolyEvent<LootTableLoad> LOOT_TABLE_LOAD = PolyEvent.create(
            handlers -> (id) -> handlers.forEach(h -> h.onLootTableLoad(id)));

    private PolyDataEvents() {}

    @FunctionalInterface
    public interface TagsUpdated
    {
        void onTagsUpdated(HolderLookup.Provider lookupProvider);
    }

    @FunctionalInterface
    public interface LootTableLoad
    {
        /** {@code id} — the resource location of the loot table being loaded. */
        void onLootTableLoad(Identifier id);
    }
}
