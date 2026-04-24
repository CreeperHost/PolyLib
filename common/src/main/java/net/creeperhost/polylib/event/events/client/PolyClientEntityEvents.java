package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class PolyClientEntityEvents
{
    /**
     * Fired when an entity is added to the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientEntityLoadNF} on {@code ClientLevel#addEntity(Entity)}.<br>
     * Fabric: {@code ClientEntityEvents.ENTITY_LOAD}.
     */
    public static final PolyEvent<EntityLoad> CLIENT_ENTITY_LOAD = PolyEvent.create(
            handlers -> (entity, level) -> handlers.forEach(h -> h.onEntityLoad(entity, level)));

    /**
     * Fired when an entity is removed from the client world.
     * <p>
     * NeoForge: mixin {@code MixinClientEntityUnloadNF} on
     * {@code ClientLevel#removeEntity(int, Entity.RemovalReason)}.<br>
     * Fabric: {@code ClientEntityEvents.ENTITY_UNLOAD}.
     */
    public static final PolyEvent<EntityUnload> CLIENT_ENTITY_UNLOAD = PolyEvent.create(
            handlers -> (entity, level) -> handlers.forEach(h -> h.onEntityUnload(entity, level)));

    private PolyClientEntityEvents() {}

    @FunctionalInterface
    public interface EntityLoad
    {
        void onEntityLoad(Entity entity, ClientLevel level);
    }

    @FunctionalInterface
    public interface EntityUnload
    {
        void onEntityUnload(Entity entity, ClientLevel level);
    }
}
