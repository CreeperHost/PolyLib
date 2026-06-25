package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * Server-side sound events.
 */
public final class PolySoundEvents
{
    /**
     * Fired when a sound is played at an entity's position.
     * Call {@link CancelContext#cancel()} to suppress the sound.
     * <p>
     * Note: Fabric has no native equivalent — this event is NeoForge-only until
     * a mixin bridge is implemented for Fabric.
     */
    public static final PolyEvent<EntitySound> ENTITY_SOUND = PolyEvent.create(handlers -> (entity, sound, source, volume, pitch, ctx) ->
    {
        for (var h : handlers)
        {
            h.onEntitySound(entity, sound, source, volume, pitch, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    private PolySoundEvents()
    {
    }

    @FunctionalInterface
    public interface EntitySound
    {
        void onEntitySound(Entity entity, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch, CancelContext ctx);
    }
}
