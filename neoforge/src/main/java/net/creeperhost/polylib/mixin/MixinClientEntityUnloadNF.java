package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientEntityEvents#CLIENT_ENTITY_UNLOAD}.
 * Fires before {@code ClientLevel#removeEntity} removes the entity so the instance is still valid.
 */
@Mixin(ClientLevel.class)
public abstract class MixinClientEntityUnloadNF
{
    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void polylib$onEntityUnload(int entityId, Entity.RemovalReason reason, CallbackInfo ci)
    {
        ClientLevel self = (ClientLevel) (Object) this;
        Entity entity = self.getEntity(entityId);
        if (entity != null)
        {
            PolyClientEntityEvents.CLIENT_ENTITY_UNLOAD.invoker().onEntityUnload(entity, self);
        }
    }
}
