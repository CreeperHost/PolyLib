package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyVillageEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyVillageEvents#SIEGE_SPAWN}.
 * Fires just before the siege attempts to spawn zombies.
 */
@Mixin(VillageSiege.class)
public abstract class FabricVillageSiegeMixin
{
    @Shadow private int spawnX;
    @Shadow private int spawnY;
    @Shadow private int spawnZ;

    @Inject(method = "trySpawn", at = @At("HEAD"), cancellable = true)
    private void polylib$onTrySpawn(ServerLevel level, CallbackInfo ci)
    {
        Vec3 pos = new Vec3(spawnX, spawnY, spawnZ);
        // Find the nearest player — VillageSiege targets the closest player to the village.
        Player player = level.getNearestPlayer(spawnX, spawnY, spawnZ, -1.0, true);
        CancelContext ctx = new CancelContext();
        PolyVillageEvents.SIEGE_SPAWN.invoker().onSiegeSpawn((VillageSiege) (Object) this, level, player, pos, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
