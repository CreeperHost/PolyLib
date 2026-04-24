package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolySpawnEvents#MOB_DESPAWN} on Fabric.
 * <p>
 * Note: {@link PolySpawnEvents.DespawnContext.Result#REMOVE} calls {@code discard()} to
 * force-remove the mob, as there is no direct Fabric API for this.
 */
@Mixin(Mob.class)
public abstract class FabricMobDespawnMixin
{
    @Inject(method = "checkDespawn", at = @At("HEAD"), cancellable = true)
    private void polylib$onCheckDespawn(CallbackInfo ci)
    {
        Mob self = (Mob) (Object) this;
        if (!(self.level() instanceof ServerLevel)) return;
        PolySpawnEvents.DespawnContext ctx = new PolySpawnEvents.DespawnContext();
        PolySpawnEvents.MOB_DESPAWN.invoker().onMobDespawn(self, ctx);
        switch (ctx.getResult())
        {
            case KEEP -> ci.cancel();
            case REMOVE ->
            {
                self.discard();
                ci.cancel();
            }
            default -> {}
        }
    }
}
