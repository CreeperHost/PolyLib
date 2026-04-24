package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bridges {@link PolySpawnEvents#FINALIZE_SPAWN} on Fabric.
 */
@Mixin(Mob.class)
public abstract class FabricFinalizeSpawnMixin
{
    @Inject(method = "finalizeSpawn", at = @At("HEAD"), cancellable = true)
    private void polylib$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                          EntitySpawnReason reason, SpawnGroupData spawnData,
                                          CallbackInfoReturnable<SpawnGroupData> cir)
    {
        CancelContext ctx = new CancelContext();
        PolySpawnEvents.FINALIZE_SPAWN.invoker().onFinalizeSpawn((Mob) (Object) this, level, ctx);
        if (ctx.isCancelled()) cir.setReturnValue(spawnData);
    }
}
