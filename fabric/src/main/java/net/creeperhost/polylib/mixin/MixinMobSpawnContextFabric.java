package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.minecraft.core.BlockPos;
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
 * Fabric bridge for {@link PolySpawnEvents#MOB_SPAWN_CONTEXT}.
 * Fires at the head of {@code Mob#finalizeSpawn} before equipment/attribute assignment.
 */
@Mixin(Mob.class)
public abstract class MixinMobSpawnContextFabric
{
    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void polylib$onMobSpawnContext(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnType,
            SpawnGroupData spawnData,
            CallbackInfoReturnable<SpawnGroupData> cir)
    {
        Mob self = (Mob) (Object) this;
        PolySpawnEvents.MOB_SPAWN_CONTEXT.invoker().onMobSpawnContext(
                self, level,
                BlockPos.containing(self.position()),
                spawnType, difficulty);
    }
}
