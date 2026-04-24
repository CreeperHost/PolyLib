package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLevelEvents#SLEEP_FINISHED}.
 * Fires when the server level's sleeping player list is updated (i.e. sleep finishes).
 */
@Mixin(ServerLevel.class)
public abstract class FabricSleepFinishedMixin
{
    @Inject(method = "updateSleepingPlayerList", at = @At("TAIL"))
    private void polylib$onUpdateSleepingPlayerList(CallbackInfo ci)
    {
        ServerLevel self = (ServerLevel) (Object) this;
        if (!self.players().stream().anyMatch(net.minecraft.world.entity.player.Player::isSleeping))
        {
            PolyLevelEvents.SLEEP_FINISHED.invoker().onSleepFinished(self);
        }
    }
}
