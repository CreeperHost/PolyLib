package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#WAKE_UP}.
 * Fires when a player wakes up from sleeping via {@code Player#stopSleepInBed}.
 */
@Mixin(Player.class)
public abstract class FabricWakeUpMixin
{
    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void polylib$onStopSleepInBed(boolean wakeImmediately, boolean updateLevel, CallbackInfo ci)
    {
        PolyPlayerEvents.WAKE_UP.invoker().onWakeUp((Player)(Object) this, updateLevel);
    }
}
