package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class PolyClientBlockEntityEvents
{
    /**
     * Fired when a block entity is added to the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientBELoadNF} on {@code ClientLevel#onBlockEntityAdded(BlockEntity)}.<br>
     * Fabric: {@code ClientBlockEntityEvents.BLOCK_ENTITY_LOAD}.
     */
    public static final PolyEvent<BlockEntityLoad> CLIENT_BLOCK_ENTITY_LOAD = PolyEvent.create(
            handlers -> (be, level) -> handlers.forEach(h -> h.onBlockEntityLoad(be, level)));

    /**
     * Fired when a block entity is removed from the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientBEUnloadNF} on {@code Level#removeBlockEntity(BlockPos)}
     * with {@code instanceof ClientLevel} guard.<br>
     * Fabric: {@code ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD}.
     */
    public static final PolyEvent<BlockEntityUnload> CLIENT_BLOCK_ENTITY_UNLOAD = PolyEvent.create(
            handlers -> (be, level) -> handlers.forEach(h -> h.onBlockEntityUnload(be, level)));

    private PolyClientBlockEntityEvents() {}

    @FunctionalInterface
    public interface BlockEntityLoad
    {
        void onBlockEntityLoad(BlockEntity blockEntity, ClientLevel level);
    }

    @FunctionalInterface
    public interface BlockEntityUnload
    {
        void onBlockEntityUnload(BlockEntity blockEntity, ClientLevel level);
    }
}
