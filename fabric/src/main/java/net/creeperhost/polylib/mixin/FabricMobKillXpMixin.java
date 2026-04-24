package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLivingEvents#MOB_KILL_XP}.
 * Allows modifying the XP value returned by {@code LivingEntity#getExperienceReward}.
 */
@Mixin(LivingEntity.class)
public abstract class FabricMobKillXpMixin
{
    @Inject(method = "getExperienceReward", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetExperienceReward(
            ServerLevel level, @Nullable Entity attacker, CallbackInfoReturnable<Integer> cir)
    {
        LivingEntity self = (LivingEntity) (Object) this;
        int[] xp = { cir.getReturnValueI() };
        PolyLivingEvents.MOB_KILL_XP.invoker().onMobKillXp(self, attacker, xp);
        if (xp[0] != cir.getReturnValueI())
        {
            cir.setReturnValue(xp[0]);
        }
    }
}
