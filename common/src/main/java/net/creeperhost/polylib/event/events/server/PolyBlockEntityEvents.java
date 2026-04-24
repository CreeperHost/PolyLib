package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Events related to block entity lifecycle (load / unload).
 */
public final class PolyBlockEntityEvents
{
    /**
     * Fired when a block entity is loaded into the world (chunk load or placement).
     * <p>
     * NeoForge: mixin on {@code Level#addBlockEntity}<br>
     * Fabric: mixin on {@code Level#addBlockEntity}
     */
    public static final PolyEvent<BlockEntityLoad> BLOCK_ENTITY_LOAD = PolyEvent.create(
            handlers -> be -> handlers.forEach(h -> h.onBlockEntityLoad(be)));

    /**
     * Fired when a block entity is unloaded from the world (chunk unload or removal).
     * <p>
     * NeoForge: mixin on {@code Level#removeBlockEntity}<br>
     * Fabric: mixin on {@code Level#removeBlockEntity}
     */
    public static final PolyEvent<BlockEntityUnload> BLOCK_ENTITY_UNLOAD = PolyEvent.create(
            handlers -> be -> handlers.forEach(h -> h.onBlockEntityUnload(be)));

    private PolyBlockEntityEvents() {}

    @FunctionalInterface
    public interface BlockEntityLoad
    {
        void onBlockEntityLoad(BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface BlockEntityUnload
    {
        void onBlockEntityUnload(BlockEntity blockEntity);
    }
}
