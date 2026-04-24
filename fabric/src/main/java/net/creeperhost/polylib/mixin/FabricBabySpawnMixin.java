package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#BABY_SPAWN}.
 * Fires when an animal produces a baby from breeding.
 */
@Mixin(Animal.class)
public abstract class FabricBabySpawnMixin
{
    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"), cancellable = true)
    private void polylib$onSpawnChildFromBreeding(ServerLevel level, Animal partner, CallbackInfo ci)
    {
        Animal self = (Animal) (Object) this;
        // child is not yet available at HEAD; pass null — informational only
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.BABY_SPAWN.invoker().onBabySpawn(self, partner, null, (AgeableMob) null, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
