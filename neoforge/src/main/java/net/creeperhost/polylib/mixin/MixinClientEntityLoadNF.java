package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientEntityEvents#CLIENT_ENTITY_LOAD}.
 * Fires after {@code ClientLevel#addEntity(Entity)} has added the entity to the world.
 */
@Mixin(ClientLevel.class)
public abstract class MixinClientEntityLoadNF
{
    @Inject(method = "addEntity", at = @At("RETURN"))
    private void polylib$onEntityLoad(Entity entity, CallbackInfo ci)
    {
        PolyClientEntityEvents.CLIENT_ENTITY_LOAD.invoker().onEntityLoad(entity, (ClientLevel) (Object) this);
    }
}
