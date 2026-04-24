package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLivingEvents#XP_DROP}.
 * Fires when a living entity drops experience on death.
 */
@Mixin(LivingEntity.class)
public abstract class FabricLivingEntityXpMixin
{
    @Shadow
    protected abstract int getExperienceReward(ServerLevel level, @Nullable Entity attacker);

    @Inject(method = "dropExperience", at = @At("HEAD"))
    private void polylib$onDropExperience(ServerLevel level, @Nullable Entity attacker, CallbackInfo ci)
    {
        int amount = getExperienceReward(level, attacker);
        if (amount > 0)
        {
            PolyLivingEvents.XP_DROP.invoker().onXpDrop((LivingEntity) (Object) this, amount);
        }
    }
}
