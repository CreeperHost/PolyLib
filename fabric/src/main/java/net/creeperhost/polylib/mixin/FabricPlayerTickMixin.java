package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerTickEvents#PLAYER_TICK_START} and
 * {@link PolyPlayerTickEvents#PLAYER_TICK_END}.
 */
@Mixin(ServerPlayer.class)
public abstract class FabricPlayerTickMixin
{
    @Inject(method = "tick", at = @At("HEAD"))
    private void polylib$onPlayerTickStart(CallbackInfo ci)
    {
        PolyPlayerTickEvents.PLAYER_TICK_START.invoker().onTick((ServerPlayer) (Object) this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void polylib$onPlayerTickEnd(CallbackInfo ci)
    {
        PolyPlayerTickEvents.PLAYER_TICK_END.invoker().onTick((ServerPlayer) (Object) this);
    }
}
