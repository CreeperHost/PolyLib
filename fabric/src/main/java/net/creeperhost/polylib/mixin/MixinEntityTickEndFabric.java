package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyEntityEvents#ENTITY_TICK_END}.
 * Fires after {@code Entity#tick} returns.
 */
@Mixin(Entity.class)
public abstract class MixinEntityTickEndFabric
{
    @Inject(method = "tick", at = @At("RETURN"))
    private void polylib$onEntityTickEnd(CallbackInfo ci)
    {
        PolyEntityEvents.ENTITY_TICK_END.invoker().onEntityTickEnd((Entity) (Object) this);
    }
}
