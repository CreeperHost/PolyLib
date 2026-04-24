package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Fabric bridge for {@link PolyPlayerEvents#PHANTOM_SPAWN}.
 * Fires just before a phantom spawn cycle begins (when {@code nextTick} reaches zero).
 * <p>
 * Cancellation stops all phantom spawns in the current cycle for the cancelled players.
 * Because the spawn loop cannot be broken per-player without complex local capture,
 * cancelling any player cancels the entire spawn action for that tick.
 */
@Mixin(PhantomSpawner.class)
public abstract class FabricPhantomSpawnMixin
{
    @Shadow private int nextTick;

    @Unique
    private final Set<UUID> polylib$cancelledPhantomPlayers = new HashSet<>();

    @Inject(method = "tick", at = @At("HEAD"))
    private void polylib$preTick(ServerLevel level, boolean spawn, CallbackInfo ci)
    {
        polylib$cancelledPhantomPlayers.clear();
        if (this.nextTick > 1) return;
        for (ServerPlayer player : level.players())
        {
            if (!player.isSpectator())
            {
                CancelContext ctx = new CancelContext();
                PolyPlayerEvents.PHANTOM_SPAWN.invoker().onPhantomSpawn(player, ctx);
                if (ctx.isCancelled())
                {
                    polylib$cancelledPhantomPlayers.add(player.getUUID());
                }
            }
        }
    }

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"),
            cancellable = true)
    private void polylib$checkPhantomCancelled(ServerLevel level, boolean spawn, CallbackInfo ci)
    {
        if (!polylib$cancelledPhantomPlayers.isEmpty())
        {
            ci.cancel();
        }
    }
}
